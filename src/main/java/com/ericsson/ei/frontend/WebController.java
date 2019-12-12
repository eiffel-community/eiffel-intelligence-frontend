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

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ericsson.ei.frontend.utils.WebControllerUtils;
import com.ericsson.eiffelcommons.helpers.RegExProvider;

@Controller
public class WebController {

    @Autowired
    private WebControllerUtils frontEndUtils;


    @RequestMapping("/")
    public String greeting(Model model) {
        model.addAttribute("frontendServiceUrl", frontEndUtils.getFrontEndServiceUrl());
        model.addAttribute("subscriptionNameRegex", RegExProvider.SUBSCRIPTION_NAME);
        model.addAttribute("notificationMetaRegex", RegExProvider.NOTIFICATION_META);
        String eiffelDocumentationUrlLinks = String.format("%s", frontEndUtils.getEiffelDocumentationUrls());
        model.addAttribute("eiffelDocumentationUrlLinks", eiffelDocumentationUrlLinks);
        return "index";
    }

    @RequestMapping("/subscriptions.html")
    public String subscription(Model model) {
        model.addAttribute("frontendServiceUrl", frontEndUtils.getFrontEndServiceUrl());
        return "subscriptions";
    }

    @RequestMapping("/test-rules.html")
    public String testRules(Model model) {
        model.addAttribute("frontendServiceUrl", frontEndUtils.getFrontEndServiceUrl());
        return "test-rules";
    }

    @RequestMapping("/information.html")
    public String info(Model model, HttpServletRequest request) {
        model.addAttribute("frontendServiceUrl", frontEndUtils.getFrontEndServiceUrl());
        model.addAttribute("version", frontEndUtils.getVersion());
        model.addAttribute("frontendAppName", frontEndUtils.getApplicationName());
        return "information";
    }

    @RequestMapping("/rules.html")
    public String rules(Model model, HttpServletRequest request) {
        model.addAttribute("frontendServiceUrl", frontEndUtils.getFrontEndServiceUrl());
        return "rules";
    }

    @RequestMapping("/login.html")
    public String login(Model model) {
        model.addAttribute("frontendServiceUrl", frontEndUtils.getFrontEndServiceUrl());
        return "login";
    }

    @RequestMapping("/add-instances.html")
    public String addInstance(Model model) {
        model.addAttribute("frontendServiceUrl", frontEndUtils.getFrontEndServiceUrl());
        return "add-instances";
    }

    @RequestMapping("/switch-backend.html")
    public String switchBackEnd(Model model) {
        model.addAttribute("frontendServiceUrl", frontEndUtils.getFrontEndServiceUrl());
        return "switch-backend";
    }

}