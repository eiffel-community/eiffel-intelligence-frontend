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
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@RestController
public class EIRequestsController {

    private static final Logger LOG = LoggerFactory.getLogger(EIRequestsController.class);

    private CloseableHttpClient client = HttpClientBuilder.create().build();

    @Autowired
    private BackEndInformation backEndInformation;

    /**
     * Bridge authorized EI Http Requests with GET method. Used for login and logout
     */
    @CrossOrigin
    @RequestMapping(value = "/auth/login", method = RequestMethod.GET)
    public ResponseEntity<String> getAuthRequests(Model model, HttpServletRequest request) {
        String eiRequestUrl = getEIRequestURL(request);

        try {
            client.close();
            client = HttpClientBuilder.create().build();
        } catch (IOException e) {
            LOG.error("Failed to close HTTP Client");
        }

        HttpGet eiRequest = new HttpGet(eiRequestUrl);

        String header = request.getHeader("Authorization");
        if (header != null) {
            eiRequest.addHeader("Authorization", header);
        }

        return getResponse(eiRequest);
    }

    /**
     * Bridge all EI Http Requests with GET method. Used for fetching
     * Subscription by id or all subscriptions and EI Env Info.
     */
    @CrossOrigin
    @RequestMapping(value = { "/subscriptions", "/subscriptions/*", "/information", "/download/*", "/auth",
        "/auth/checkStatus", "/auth/logout", "/queryAggregatedObject", "/queryMissedNotifications", "/query" }, method = RequestMethod.GET)
    public ResponseEntity<String> getRequests(Model model, HttpServletRequest request) {
        String eiRequestUrl = getEIRequestURL(request);

        HttpGet eiRequest = new HttpGet(eiRequestUrl);

        return getResponse(eiRequest);
    }

    /**
     * Bridge all EI Http Requests with POST method.
     */
    @CrossOrigin
    @RequestMapping(value = { "/subscriptions", "/rules/rule-check/aggregation", "/query" }, method = RequestMethod.POST)
    public ResponseEntity<String> postRequests(Model model, HttpServletRequest request) {
        String eiRequestUrl = getEIRequestURL(request);

        String inputReqJsonContent = "";
        try {
            BufferedReader inputBufReader = new BufferedReader(request.getReader());
            for (String line = inputBufReader.readLine(); line != null; line = inputBufReader.readLine()) {
                inputReqJsonContent += line;
            }
            inputBufReader.close();
        } catch (IOException e) {
            LOG.error("Forward Request Errors: " + e);
        }

        LOG.debug("Input Request JSON Content to be forwarded:\n" + inputReqJsonContent);
        HttpEntity inputReqJsonEntity = new ByteArrayEntity(inputReqJsonContent.getBytes());

        HttpPost eiRequest = new HttpPost(eiRequestUrl);
        eiRequest.setEntity(inputReqJsonEntity);
        eiRequest.setHeader("Content-type", "application/json");

        return getResponse(eiRequest);
    }

    /**
     * Bridge all EI Http Requests with PUT method. E.g. Making Update
     * Subscription Request.
     */
    @CrossOrigin
    @RequestMapping(value = "/subscriptions", method = RequestMethod.PUT)
    public ResponseEntity<String> putRequests(Model model, HttpServletRequest request) {
        String eiRequestUrl = getEIRequestURL(request);

        String inputReqJsonContent = "";
        try {
            BufferedReader inputBufReader = new BufferedReader(request.getReader());
            for (String line = inputBufReader.readLine(); line != null; line = inputBufReader.readLine()) {
                inputReqJsonContent += line;
            }
            inputBufReader.close();
        } catch (IOException e) {
            LOG.error("Forward Request Errors: " + e);
        }

        LOG.debug("Input Request JSON Content to be forwarded:\n" + inputReqJsonContent);
        HttpEntity inputReqJsonEntity = new ByteArrayEntity(inputReqJsonContent.getBytes());

        HttpPut eiRequest = new HttpPut(eiRequestUrl);
        eiRequest.setEntity(inputReqJsonEntity);
        eiRequest.setHeader("Content-type", "application/json");

        return getResponse(eiRequest);
    }

    /**
     * Bridge all EI Http Requests with DELETE method. Used for DELETE
     * subscriptions.
     */
    @CrossOrigin
    @RequestMapping(value = "/subscriptions/*", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteRequests(Model model, HttpServletRequest request) {
        String eiRequestUrl = getEIRequestURL(request);

        HttpDelete eiRequest = new HttpDelete(eiRequestUrl);

        return getResponse(eiRequest);
    }

    private String getEIBackendSubscriptionAddress() {
        String httpMethod = "http";
        if (backEndInformation.isHttps()) {
            httpMethod = "https";
        }

        if (backEndInformation.getPath() != null && !backEndInformation.getPath().isEmpty()) {
            return httpMethod + "://" + backEndInformation.getHost() + ":" + backEndInformation.getPort() + "/"
                    + backEndInformation.getPath();
        }
        return httpMethod + "://" + backEndInformation.getHost() + ":" + backEndInformation.getPort();
    }

    private String getEIRequestURL(HttpServletRequest request) {
        String eiBackendAddressSuffix = request.getServletPath();
        String requestQuery = request.getQueryString();
        String query = (requestQuery != null && !requestQuery.isEmpty()) ? "?" + requestQuery : "";
        String requestUrl = getEIBackendSubscriptionAddress() + eiBackendAddressSuffix + query;
        LOG.info("Got HTTP Request with method " + request.getMethod()
            + "\nUrlSuffix: " + eiBackendAddressSuffix
            + "\nForwarding Request to EI Backend with url: " + requestUrl);
        return requestUrl;
    }

    private ResponseEntity<String> getResponse(HttpRequestBase request) {
        String jsonContent = "";
        int statusCode = 102;
        try (CloseableHttpResponse eiResponse = client.execute(request)) {
            InputStream inStream = eiResponse.getEntity().getContent();
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
            for (String line = bufReader.readLine(); line != null; line = bufReader.readLine()) {
                jsonContent += line;
            }
            if (jsonContent.isEmpty()) {
                jsonContent = "[]";
            }
            statusCode = eiResponse.getStatusLine().getStatusCode();
            LOG.info("EI Http Reponse Status Code: " + eiResponse.getStatusLine().getStatusCode()
                    + "\nEI Recevied jsonContent:\n" + jsonContent
                    + "\nForwarding response back to EI Frontend WebUI.");
            bufReader.close();
            inStream.close();
        } catch (IOException e) {
            LOG.error("Forward Request Errors: " + e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(jsonContent, headers, HttpStatus.valueOf(statusCode));
    }

}