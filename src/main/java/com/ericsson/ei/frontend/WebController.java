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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    @Value("${ei.frontendServiceHost}")
    private String frontendServiceHost;

    @Value("${ei.frontendServicePort}")
    private int frontendServicePort;

    @Value("${ei.frontendContextPath}")
    private String frontendContextPath;

    @Value("${ei.eiffelDocumentationUrls}")
    private String eiffelDocumentationUrls;

    @Value("${ei.useSecureHttpFrontend}")
    private boolean useSecureHttpFrontend;

    @Autowired
    private BackEndInformation backEndInformation;

    @RequestMapping("/")
    public String greeting(Model model) {
        model.addAttribute("frontendServiceUrl", getFrontendServiceUrl());  // inject in DOM for AJAX etc
        String eiffelDocumentationUrlLinks = String.format("%s", eiffelDocumentationUrls);
        model.addAttribute("eiffelDocumentationUrlLinks", eiffelDocumentationUrlLinks);  // inject in DOM for AJAX etc
        return "index";
    }

    @RequestMapping("/subscriptionpage.html")
    public String subscription(Model model) {
        model.addAttribute("frontendServiceUrl", getFrontendServiceUrl());  // inject in DOM for AJAX etc
        return "subscription";
    }

    @RequestMapping("/testRules.html")
    public String testRules(Model model) {
        model.addAttribute("frontendServiceUrl", getFrontendServiceUrl());  // inject in DOM for AJAX etc
        return "testRules";
    }

    @RequestMapping("/eiInfo.html")
    public String eiInfo(Model model) {
        model.addAttribute("frontendServiceUrl", getFrontendServiceUrl());  // inject in DOM for AJAX etc
        String backendServerUrl = String.format("http://%s:%s", backEndInformation.getHost(), backEndInformation.getPort());
        model.addAttribute("backendServerUrl", backendServerUrl);
        return "eiInfo";
    }

    @RequestMapping("/login.html")
    public String login(Model model) {
        model.addAttribute("frontendServiceUrl", getFrontendServiceUrl());  // inject in DOM for AJAX etc
        return "login";
    }

    // Added documentation for JMESPath rules usage
    @RequestMapping("/jmesPathRulesSetUp.html")
    public String jmesPathRulesSetUp(Model model) {
        return "jmesPathRulesSetUp";
    }

    @RequestMapping("/add-instances.html")
    public String addInstance(Model model) {
        model.addAttribute("frontendServiceUrl", getFrontendServiceUrl());  // inject in DOM for AJAX etc
        return "add-instances";
    }

    @RequestMapping("/switch-backend.html")
    public String switchBackEnd(Model model) {
        model.addAttribute("frontendServiceUrl", getFrontendServiceUrl());  // inject in DOM for AJAX etc
        return "switch-backend";
    }

    private String getFrontendServiceUrl() {
        String httpMethod = "http";
        if (useSecureHttpFrontend) {
            httpMethod = "https";
        }
        String frontendServiceUrl;
        if (frontendContextPath != null && !frontendContextPath.isEmpty()) {
            frontendServiceUrl = String.format("%s://%s:%d/%s", httpMethod, frontendServiceHost, frontendServicePort, frontendContextPath);
        } else {
            frontendServiceUrl = String.format("%s://%s:%d", httpMethod, frontendServiceHost, frontendServicePort);
        }
        return frontendServiceUrl;
    }

}