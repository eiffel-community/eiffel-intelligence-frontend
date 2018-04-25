/*
   Copyright 2017 Ericsson AB.
   For a full list of individual contributors, please see the commit history.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.ericsson.ei.frontend;


import com.ericsson.ei.frontend.model.BackEndInformation;
import com.ericsson.ei.frontend.model.Index;
import com.ericsson.ei.frontend.model.ListWrapper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WebController {

    private static final Logger LOG = LoggerFactory.getLogger(WebController.class);

    @Value("${ei.frontendServiceHost}")
    private String frontendServiceHost;

    @Value("${ei.frontendServicePort}")
    private int frontendServicePort;

    @Value("${ei.frontendContextPath}")
    private String frontendContextPath;

    @Value("${ei.eiffelDocumentationUrls}")
    private String eiffelDocumentationUrls;

    @Value("${ei.backendInstancesPath}")
    private String eiInstancesPath;

    @Value("${ei.backendServerHost}")
    private String host;

    @Value("${ei.backendServerPort}")
    private int port;

    @Value("${ei.backendContextPath}")
    private String path;

    @Value("${ei.useSecureHttp}")
    private boolean https;

    @Autowired
    private BackEndInformation backEndInformation;

    @Autowired
    private ListWrapper wrapper;

    @Autowired
    private Index index;

    private List<BackEndInformation> information = new ArrayList<>();

    @PostConstruct
    public void init() {
        index.setIndex(0);
        information.add(backEndInformation);
        if (eiInstancesPath != null) {
            try {
                JSONArray inputBackEndInstances = new JSONArray(new String(Files.readAllBytes(Paths.get(eiInstancesPath))));
                for (Object o : inputBackEndInstances) {
                    JSONObject instance = (JSONObject) o;
                    BackEndInformation backEndInformations = new ObjectMapper().readValue(instance.toString(), BackEndInformation.class);
                    if (!checkIfInstanceAlreadyExist(backEndInformations)) {
                        information.add(backEndInformations);
                    }
                }
            } catch (IOException e) {
                LOG.error("Failure when try to parse json file" + e.getMessage());
            }
        }
        writeIntoFile();
    }

    @RequestMapping("/")
    public String greeting(Model model) {
        String eiffelDocumentationUrlLinks = String.format("%s", eiffelDocumentationUrls);
        model.addAttribute("eiffelDocumentationUrlLinks", eiffelDocumentationUrlLinks);  // inject in DOM for AJAX etc
        return "index";
    }

    @RequestMapping("/subscriptionpage.html")
    public String subscription(Model model) {
        String httpMethod = "http";
        if (backEndInformation.isHttps()) {
            httpMethod = "https";
        }
        String frontendServiceUrl;
        if (frontendContextPath != null && !frontendContextPath.isEmpty()) {
            frontendServiceUrl = String.format("%s://%s:%d/%s", httpMethod, frontendServiceHost, frontendServicePort, frontendContextPath);
        } else {
            frontendServiceUrl = String.format("%s://%s:%d", httpMethod, frontendServiceHost, frontendServicePort);
        }
        model.addAttribute("frontendServiceUrl", frontendServiceUrl);  // inject in DOM for AJAX etc
        return "subscription";
    }

    @RequestMapping("/testRules.html")
    public String testRules(Model model) {
        return "testRules";
    }

    @RequestMapping("/eiInfo.html")
    public String eiInfo(Model model) {
        String frontendServiceUrl = String.format("http://%s:%d", frontendServiceHost, frontendServicePort);
        model.addAttribute("frontendServiceUrl", frontendServiceUrl);  // inject in DOM for AJAX etc
        String backendServerUrl = String.format("http://%s:%d", backEndInformation.getHost(), backEndInformation.getPort());
        model.addAttribute("backendServerUrl", backendServerUrl);
        return "eiInfo";
    }

    @RequestMapping("/login.html")
    public String login(Model model) {
        return "login";
    }

    @RequestMapping("/register.html")
    public String register(Model model) {
        return "register";
    }

    @RequestMapping("/forgot-password.html")
    public String forgotPassword(Model model) {
        return "forgot-password";
    }

    // Added documentation for JMESPath rules usage
    @RequestMapping("/jmesPathRulesSetUp.html")
    public String jmesPathRulesSetUp(Model model) {
        return "jmesPathRulesSetUp";
    }

    @RequestMapping(value = "/switch-backend", method = RequestMethod.GET)
    public String switchBackEnd(Model model) {
        wrapper.setBackEndInformation(information);
        model.addAttribute("listWrapper", wrapper);
        model.addAttribute("index", index);
        return "switch-backend";
    }

    @RequestMapping(params = "switch", value = "/switch-backend", method = RequestMethod.POST)
    public String switchBackEndInstance(@ModelAttribute("index") Index index, Model model) {
        this.index.setIndex(index.getIndex());
        setBackEndProperties(index);
        information.set(0, getBackEndProperties());
        return "redirect:/";
    }

    @RequestMapping(params = "delete", value = "/switch-backend", method = RequestMethod.POST)
    public String deleteBackEndInstance(@ModelAttribute("index") Index index, Model model) {
        information.remove(index.getIndex());
        writeIntoFile();
        wrapper.setBackEndInformation(information);
        model.addAttribute("listWrapper", wrapper);
        model.addAttribute("index", index);
        return "switch-backend.html";
    }

    @RequestMapping(value = "/add-instances", method = RequestMethod.GET)
    public String addInstance(Model model) {
        model.addAttribute("backendinformation", new BackEndInformation());
        return "add-instances";
    }

    @RequestMapping(value = "/add-instances", method = RequestMethod.POST)
    public String addInstanceInformation(@Valid @ModelAttribute(value = "backendinformation") BackEndInformation backEndInfo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/add-instances";
        } else {
            information.add(backEndInfo);
            if (!checkIfInstanceAlreadyExist(backEndInfo)) {
                writeIntoFile();
            }
        }
        return "/switch-backend";
    }

    private BackEndInformation getBackEndProperties() {
        BackEndInformation backEndInformationFromProperties = new BackEndInformation();
        backEndInformationFromProperties.setHost(host);
        backEndInformationFromProperties.setPort(port);
        backEndInformationFromProperties.setPath(path);
        backEndInformationFromProperties.setHttps(https);
        return backEndInformationFromProperties;
    }

    private void setBackEndProperties(Index index) {
        backEndInformation.setName(information.get(index.getIndex()).getName());
        backEndInformation.setHost(information.get(index.getIndex()).getHost());
        backEndInformation.setPort(information.get(index.getIndex()).getPort());
        backEndInformation.setPath(information.get(index.getIndex()).getPath());
        backEndInformation.setHttps(information.get(index.getIndex()).isHttps());
    }

    private boolean checkIfInstanceAlreadyExist(BackEndInformation backEndInformation) {
        return backEndInformation.getHost().equals(host) && backEndInformation.getPort() == port;
    }

    private void writeIntoFile() {
        if (eiInstancesPath != null) {
            ObjectMapper mapper = new ObjectMapper();
            try (JsonGenerator add = mapper.getFactory().createGenerator(new FileOutputStream(eiInstancesPath))) {
                mapper.writeValue(add, information);
            } catch (IOException e) {
                LOG.error("Couldn't add instance to file " + e.getMessage());
            }
        }
    }
}