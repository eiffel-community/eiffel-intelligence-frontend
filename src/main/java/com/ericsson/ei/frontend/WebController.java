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

import com.ericsson.ei.frontend.utils.WebControllerUtils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    @Autowired
    private WebControllerUtils frontEndUtils;

    @RequestMapping("/")
    public String greeting(Model model) {
        model.addAttribute("frontendServiceUrl", frontEndUtils.getFrontendServiceUrl());
        String eiffelDocumentationUrlLinks = String.format("%s", frontEndUtils.getEiffelDocumentationUrls());
        model.addAttribute("eiffelDocumentationUrlLinks", eiffelDocumentationUrlLinks);
        return "index";
    }

    @RequestMapping("/subscriptionpage.html")
    public String subscription(Model model) {
        model.addAttribute("frontendServiceUrl", frontEndUtils.getFrontendServiceUrl());
        return "subscription";
    }

    @RequestMapping("/testRules.html")
    public String testRules(Model model) {
        model.addAttribute("frontendServiceUrl", frontEndUtils.getFrontendServiceUrl());
        return "testRules";
    }

    @RequestMapping("/eiInfo.html")
    public String eiInfo(Model model, HttpServletRequest request) {
        model.addAttribute("frontendServiceUrl", frontEndUtils.getFrontendServiceUrl());
        model.addAttribute("backendServerUrl", frontEndUtils.getBackEndServiceUrl(request.getSession()));
        return "eiInfo";
    }

    @RequestMapping("/login.html")
    public String login(Model model) {
        model.addAttribute("frontendServiceUrl", frontEndUtils.getFrontendServiceUrl());
        return "login";
    }

    // Added documentation for JMESPath rules usage
    @RequestMapping("/jmesPathRulesSetUp.html")
    public String jmesPathRulesSetUp(Model model) {
        return "jmesPathRulesSetUp";
    }

    @RequestMapping("/add-instances.html")
    public String addInstance(Model model) {
        model.addAttribute("frontendServiceUrl", frontEndUtils.getFrontendServiceUrl());
        return "add-instances";
    }

    @RequestMapping("/switch-backend.html")
    public String switchBackEnd(Model model) {
        model.addAttribute("frontendServiceUrl", frontEndUtils.getFrontendServiceUrl());
        return "switch-backend";
    }

}