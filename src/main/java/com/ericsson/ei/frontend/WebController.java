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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    private JSONArray instances = new JSONArray();

    @PostConstruct
    public void init() {
        instances.put(getCurrentInstance());
//        index.setIndex(0);
//        information.add(backEndInformation);
//        if (eiInstancesPath != null) {
//            try {
//                JSONArray inputBackEndInstances = new JSONArray(new String(Files.readAllBytes(Paths.get(eiInstancesPath))));
//                for (Object o : inputBackEndInstances) {
//                    JSONObject instance = (JSONObject) o;
//                    BackEndInformation backEndInformations = new ObjectMapper().readValue(instance.toString(), BackEndInformation.class);
//                    if (!checkIfInstanceAlreadyExist(backEndInformations)) {
//                        information.add(backEndInformations);
//                    }
//                }
//            } catch (IOException e) {
//                LOG.error("Failure when try to parse json file" + e.getMessage());
//            }
//        }
//        writeIntoFile();
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

    @RequestMapping("/switch-backend.html")
    public String switchBackEnd(Model model) {
        return "switch-backend";
    }

    @RequestMapping("/add-instances.html")
    public String addInstance(Model model) {
        return "add-instances";
    }

    @RequestMapping(value = "/switch-backend", method = RequestMethod.POST)
    public ResponseEntity<String> switchBackEndInstance(Model model, HttpServletRequest request) {
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            instances = new JSONArray(body);
            LOG.info(instances.toString());
//            for(int i = 0; i < instances.length(); i++) {
//                if(instances.getJSONObject(i).get("checked").equals(true) {
//                    instances.getJSONObject(i);
//                    break;
//                }
//            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/add-instances", method = RequestMethod.POST)
    public ResponseEntity<String> addInstanceInformation(Model model, HttpServletRequest request) {
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            JSONObject instance = new JSONObject(body);
            if(!checkIfInstanceAlreadyExist(instance)) {
                instance.put("checked", false);
                instances.put(instance);
                LOG.info(instances.toString());
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Instance already exist", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/get-instances", method = RequestMethod.GET)
    public ResponseEntity<String> getInstances(Model model) {
        return new ResponseEntity<>(instances.toString(), HttpStatus.OK);
    }

    private JSONObject getCurrentInstance() {
        JSONObject instance = new JSONObject();
        instance.put("name", "core");
        instance.put("host", host);
        instance.put("port", port);
        instance.put("path", path);
        instance.put("https", https);
        instance.put("checked", true);
        return instance;
    }

    private void setBackEndProperties(JSONObject instance) {
        backEndInformation.setName(information.get(index.getIndex()).getName());
        backEndInformation.setHost(information.get(index.getIndex()).getHost());
        backEndInformation.setPort(information.get(index.getIndex()).getPort());
        backEndInformation.setPath(information.get(index.getIndex()).getPath());
        backEndInformation.setHttps(information.get(index.getIndex()).isHttps());
    }

    private boolean checkIfInstanceAlreadyExist(JSONObject instance) {
        for(int i = 0; i < instances.length(); i++) {
            if(instances.getJSONObject(i).get("host").equals(instance.get("host")) &&
                instances.getJSONObject(i).get("port").equals(instance.get("port"))) {
                return true;
            }
        }
        return false;
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