/*
   Copyright 2018 Ericsson AB.
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
package com.ericsson.ei.frontend.utils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ericsson.ei.frontend.model.BackEndInformation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Component
public class WebControllerUtils {

    private static final Logger LOG = LoggerFactory.getLogger(WebControllerUtils.class);

    @Value("${ei.frontendServiceHost}")
    private String frontendServiceHost;

    @Setter
    @Value("${ei.frontendServicePort}")
    private int frontendServicePort;

    @Value("${ei.frontendContextPath}")
    private String frontendContextPath;

    @Value("${ei.useSecureHttpFrontend}")
    private boolean useSecureHttpFrontend;

    @Value("${ei.eiffelDocumentationUrls}")
    private String eiffelDocumentationUrls;

    private String frontendServiceUrl;

    @Autowired
    private BackEndInstancesUtils backEndInstancesUtils;

    @PostConstruct
    public void init() {
        LOG.info("Initiating FrontEndUtils.");
        setFrontEndServiceUrl();

    }

    private void setFrontEndServiceUrl() {
        String httpMethod = "http";

        if (useSecureHttpFrontend) {
            httpMethod = "https";
        }

        if (frontendContextPath != null && !frontendContextPath.isEmpty()) {
            frontendServiceUrl = String.format("%s://%s:%d/%s", httpMethod, frontendServiceHost, frontendServicePort,
                    frontendContextPath);
        } else {
            frontendServiceUrl = String.format("%s://%s:%d", httpMethod, frontendServiceHost, frontendServicePort);
        }
    }

    public String getBackEndServiceUrl(HttpSession httpSession) {
        String activeInstance = null;
        if (httpSession.getAttribute("backEndInstanceName") != null) {
            activeInstance = httpSession.getAttribute("backEndInstanceName").toString();
        }

        BackEndInformation backEndInformation = backEndInstancesUtils.getBackEndInformationByName(activeInstance);

        return backEndInformation.getUrlAsString();
    }

}