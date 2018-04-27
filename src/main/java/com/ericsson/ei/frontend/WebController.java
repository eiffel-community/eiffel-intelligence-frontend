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
import com.ericsson.ei.frontend.utils.BackEndInstancesUtils;
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

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    private BackEndInformation backEndInformation;

    @Autowired
    private BackEndInstancesUtils utils;

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
            utils.setInstances(new JSONArray(body));
            utils.writeIntoFile();
            utils.parseBackEndInstancesFile();
            for (BackEndInformation backEndInformation : utils.getInformation()) {
                if (backEndInformation.isChecked()) {
                    utils.setBackEndProperties(backEndInformation);
                }
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/switch-backend", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteBackEndInstance(Model model, HttpServletRequest request) {
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            utils.setInstances(new JSONArray(body));
            utils.writeIntoFile();
            utils.parseBackEndInstancesFile();
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
            if (!utils.checkIfInstanceAlreadyExist(instance)) {
                instance.put("checked", false);
                utils.getInstances().put(instance);
                utils.writeIntoFile();
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
        return new ResponseEntity<>(utils.getInstances().toString(), HttpStatus.OK);
    }
}