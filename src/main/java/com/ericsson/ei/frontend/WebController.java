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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Controller
@ConfigurationProperties(prefix="ei")
public class WebController {

    private String frontendServiceHost;
    private int frontendServicePort;


    @RequestMapping("/")
    public String greeting(Model model) {

//        String frontendServiceUrl = String.format("http://%s:%d", frontendServiceHost, frontendServicePort);

//        model.addAttribute("frontendServiceUrl", frontendServiceUrl);  // inject in DOM for AJAX etc

        return "index";
    }
    
    @RequestMapping("/subscriptionpage.html")
    public String subscription(Model model) {

        String frontendServiceUrl = String.format("http://%s:%d", frontendServiceHost, frontendServicePort);

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
    

    // Backend host and port (Getter & Setters), application.properties -> greeting.xxx
    public String getFrontendServiceHost() {
        return frontendServiceHost;
    }

    public void setFrontendServiceHost(String frontendServiceHost) {
        this.frontendServiceHost = frontendServiceHost;
    }

    public int getFrontendServicePort() {
        return frontendServicePort;
    }

    public void setFrontendServicePort(int frontendServicePort) {
        this.frontendServicePort = frontendServicePort;
    }

}