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
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ericsson.ei.frontend.exceptions.EiBackendInstancesException;
import com.ericsson.ei.frontend.model.BackEndInformation;

import lombok.Getter;

@Getter
@Component
public class EIRequestsControllerUtils {

    private static final Logger LOG = LoggerFactory.getLogger(EIRequestsControllerUtils.class);

    private static final String BACKEND_URL_KEY_NAME = "backendurl";
    private static final String BACKEND_NAME_KEY_NAME = "backendname";

    @Autowired
    private BackEndInstancesUtils backEndInstancesUtils;

    /**
     * Processes an HttpServletRequest and extract the URL parameters from it and reformats the requests and removes EI
     * Front End specific parameters if any and builds a new URL to be used to call the requested, selected or default
     * EI back end.
     *
     * @param request
     * @return String
     * @throws Exception 
     * @throws IOException
     */
    public String getEIRequestURL(HttpServletRequest request) throws EiBackendInstancesException {
        String eiBackendAddressSuffix = request.getServletPath();
        String requestQuery = request.getQueryString();

        String requestUrl = getBackEndUrl(requestQuery);
        requestQuery = removeBackendDataFromQueryString(requestQuery);

        if (!requestQuery.isEmpty()) {
            String query = "?" + requestQuery;
            requestUrl = requestUrl + eiBackendAddressSuffix + query;
        } else {
            requestUrl = requestUrl + eiBackendAddressSuffix;
        }

        LOG.debug("Got HTTP Request with method " + request.getMethod() + "\nUrlSuffix: " + eiBackendAddressSuffix
                + "\nForwarding Request to EI Backend with url: " + requestUrl);

        return requestUrl;
    }

    private String getBackEndUrl(String requestQuery) throws EiBackendInstancesException {
        String requestUrl = null;

        if (requestQuery != null) {
            List<NameValuePair> params = getParameters(requestQuery);
            if (requestQuery.contains(BACKEND_URL_KEY_NAME)) {
                requestUrl = extractUrlFromParameters(params);
            } else {
                String backEndName = extractBackEndNameFromParameters(params);
                BackEndInformation backEndInformation = backEndInstancesUtils.getBackEndInformationByName(backEndName);
                if (backEndInformation == null) {
                    throw new EiBackendInstancesException("No EI Backend instances has been configured for EI Frontend service.");
                }
                requestUrl = backEndInformation.getUrlAsString();
            }
        } else {
            BackEndInformation backEndInformation = backEndInstancesUtils.getBackEndInformationByName(null);
            requestUrl = backEndInformation.getUrlAsString();
        }

        LOG.debug("Forwarding request to url '" + requestUrl + "'.");
        return requestUrl;
    }

    private List<NameValuePair> getParameters(String requestQuery) {
        List<NameValuePair> params = null;

        try {
            params = URLEncodedUtils.parse(new URI("?" + requestQuery), Charset.forName("UTF-8"));
        } catch (URISyntaxException e) {
            LOG.error("Error while encoding URL parameters: " + e);
        }

        return params;
    }

    private String extractUrlFromParameters(List<NameValuePair> params) {
        String urlFromParams = null;

        for (NameValuePair param : params) {
            if (param.getName()
                     .equals(BACKEND_URL_KEY_NAME)) {
                try {
                    urlFromParams = URLDecoder.decode(param.getValue(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    LOG.error("Failed to decode back-end url");
                }
            }
        }

        return urlFromParams;
    }

    private String extractBackEndNameFromParameters(List<NameValuePair> params) {
        String backendName = null;

        for (NameValuePair param : params) {
            if (param.getName()
                     .equals(BACKEND_NAME_KEY_NAME)) {
                try {
                    backendName = URLDecoder.decode(param.getValue(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    LOG.error("Failed to decode back-end name");
                }
            }
        }

        return backendName;
    }

    private String removeBackendDataFromQueryString(String requestQuery) {
        List<NameValuePair> params = getParameters(requestQuery);
        List<NameValuePair> processedParams = new ArrayList<>();
        for (NameValuePair param : params) {
            String name = param.getName(), value = param.getValue();
            if (name == null || value == null || name.equals(BACKEND_URL_KEY_NAME) || name.equals(BACKEND_NAME_KEY_NAME)) {
                continue;
            }
            processedParams.add(new BasicNameValuePair(name, value));
        }

        if (processedParams.size() == 0) {
            return "";
        }

        return URLEncodedUtils.format(processedParams, "UTF8");
    }
}