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