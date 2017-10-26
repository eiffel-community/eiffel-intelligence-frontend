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

    private String backendServiceHost;
    private int backendServicePort;


    @RequestMapping("/")
    public String greeting(Model model) {

        String backendServiceUrl = String.format("http://%s:%d", backendServiceHost, backendServicePort);

        model.addAttribute("backendServiceUrl", backendServiceUrl);  // inject in DOM for AJAX etc

        return "index";
    }

    // Backend host and port (Getter & Setters), application.properties -> greeting.xxx
    public String getBackendServiceHost() {
        return backendServiceHost;
    }

    public void setBackendServiceHost(String backendServiceHost) {
        this.backendServiceHost = backendServiceHost;
    }

    public int getBackendServicePort() {
        return backendServicePort;
    }

    public void setBackendServicePort(int backendServicePort) {
        this.backendServicePort = backendServicePort;
    }

}