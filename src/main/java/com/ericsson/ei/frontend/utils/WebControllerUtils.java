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

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ericsson.ei.frontend.model.BackEndInformation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Component
@NoArgsConstructor
@AllArgsConstructor
public class WebControllerUtils {

    @Value("${ei.frontendServiceHost:#{null}}")
    private String frontendServiceHost;

    @Value("${ei.frontendServicePort:#{null}}")
    private int frontendServicePort;

    @Value("${ei.frontendContextPath:#{null}}")
    private String frontendContextPath;

    @Value("${ei.useSecureHttpFrontend:#{null}}")
    private boolean useSecureHttpFrontend;

    @Value("${ei.eiffelDocumentationUrls:#{null}}")
    private String eiffelDocumentationUrls;

    @Value("${build.version.name::#{null}}")
    private String EnterpriseVersionName;

    @Value("${build.version:#{null}}")
    private String EnterpriseVersion;

    private String version;

    private String applicationName;

    @Autowired
    private BackEndInstancesUtils backEndInstancesUtils;

    @PostConstruct
    public void init() throws IOException {
        Properties properties = new Properties();
        properties.load(WebControllerUtils.class.getResourceAsStream("/default-application.properties"));
        version = properties.getProperty("version");
        applicationName = properties.getProperty("artifactId");
    }

    /**
     * Formats the parameters in the class to an URL as String.
     *
     * @return String
     *      URL to this service
     */
    public String getFrontEndServiceUrl() {
        String requestedUrl = null;
        String http = "http";
        String contextPath = "";
        if (useSecureHttpFrontend) {
        	http = "https";
        }

        if (frontendContextPath != null && !frontendContextPath.isEmpty()) {
        	contextPath = ("/" + frontendContextPath).replace("//", "/");
        }

        requestedUrl = String.format("%s://%s:%d%s", http, frontendServiceHost, frontendServicePort, contextPath);

        return requestedUrl;
    }

    /**
     * Extract the back end instance name from the session if any, and requests the
     * BackendInformation for this name, or null if no name was found.
     *
     * @param httpSession
     * @return String
     *      URL from found BackendInformation
     */
    public String getBackEndServiceUrl(HttpSession httpSession) {
        String activeInstance = null;
        if (httpSession.getAttribute("backEndInstanceName") != null) {
            activeInstance = httpSession.getAttribute("backEndInstanceName").toString();
        }

        BackEndInformation backEndInformation = backEndInstancesUtils.getBackEndInformationByName(activeInstance);

        return backEndInformation.getUrlAsString();
    }

}