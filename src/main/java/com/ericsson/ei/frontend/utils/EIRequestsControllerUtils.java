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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ericsson.ei.frontend.model.BackEndInformation;

import lombok.Getter;

@Getter
@Component
public class EIRequestsControllerUtils {

    private static final Logger LOG = LoggerFactory.getLogger(EIRequestsControllerUtils.class);

    private static final List<String> REQUESTS_WITH_QUERY_PARAM = new ArrayList<>(Arrays.asList("/queryAggregatedObject", "/queryMissedNotifications", "/query"));
    private static final String BACKEND_KEY_NAME = "backendurl";

    @Autowired
    private BackEndInstancesUtils backEndInstancesUtils;

    /**
     * Processes an HttpServletRequest and extract the URL parameters from it and reformats
     * the requests and removes EI Front End specific parameters if any and builds a new URL
     * to be used to call the requested, selected or default EI back end.
     *
     * @param request
     * @return String
     * @throws IOException
     */
    public String getEIRequestURL(HttpServletRequest request) {
        String eiBackendAddressSuffix = request.getServletPath();
        String requestQuery = request.getQueryString();
        String requestUrl = null;

        if (requestQuery != null && requestQuery.contains(BACKEND_KEY_NAME)) {
            // Selecting back end from user input as parameter.
            List<NameValuePair> params = getParameters(requestQuery);
            requestUrl = extractUrlFromParameters(params);
            requestQuery = removeBackendDataFromQueryString(params);
            LOG.info(BACKEND_KEY_NAME + " key detected, forwarding request to url '" + requestUrl + "'.");
        } else {
            BackEndInformation backEndInformation = getEIBackendInformation(request);
            requestUrl = backEndInformation.getUrlAsString();
        }

        if(REQUESTS_WITH_QUERY_PARAM.contains(eiBackendAddressSuffix)) {
            String query = (requestQuery != null && !requestQuery.isEmpty()) ? "?" + requestQuery : "";
            requestUrl = requestUrl + eiBackendAddressSuffix + query;
        } else {
            requestUrl = requestUrl + eiBackendAddressSuffix;
        }
        LOG.debug("Got HTTP Request with method " + request.getMethod() + "\nUrlSuffix: " + eiBackendAddressSuffix
                + "\nForwarding Request to EI Backend with url: " + requestUrl);
        return requestUrl;
    }

    private BackEndInformation getEIBackendInformation(HttpServletRequest request) {
        String backEndInstanceName = null;

        if (request.getSession().getAttribute("backEndInstanceName") != null) {
            backEndInstanceName = request.getSession().getAttribute("backEndInstanceName").toString();
        }

        return backEndInstancesUtils.getBackEndInformationByName(backEndInstanceName);
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
            if (param.getName().equals(BACKEND_KEY_NAME)) {
                urlFromParams = param.getValue();
            }
        }
        return urlFromParams;
    }

    private String removeBackendDataFromQueryString(List<NameValuePair> params) {
        List<NameValuePair> processedParams = new ArrayList<>();
        for (NameValuePair param : params) {
            String name = param.getName(), value = param.getValue();
            if (name.equals(BACKEND_KEY_NAME)) {
                continue;
            }
            processedParams.add(new BasicNameValuePair(name, value));
        }

        if (processedParams.size() == 0)
            return null;

        return URLEncodedUtils.format(processedParams, "UTF8");
    }
}