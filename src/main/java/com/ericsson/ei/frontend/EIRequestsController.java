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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
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

import com.ericsson.ei.frontend.utils.EIRequestsControllerUtils;
@RestController
public class EIRequestsController {

    private static final Logger LOG = LoggerFactory.getLogger(EIRequestsController.class);
    private static final String X_AUTH_TOKEN = "x-auth-token";
    List<String> HEADERS_TO_COPY = new ArrayList<>(Arrays.asList(X_AUTH_TOKEN, "authorization"));

    @Autowired
    private EIRequestsControllerUtils eiRequestsControllerUtils;

    /**
     * Bridge all EI Http Requests with GET method. Used for fetching
     * Subscription by id or all subscriptions and EI Env Info.
     */
    @CrossOrigin
    @RequestMapping(value = { "/subscriptions", "/subscriptions/*", "/information", "/download/*", "/auth",
        "/auth/*", "/queryAggregatedObject", "/queryMissedNotifications", "/query", "/rules/rule-check/testRulePageEnabled" }, method = RequestMethod.GET)
    public ResponseEntity<String> getRequests(Model model, HttpServletRequest request) {
        String eiRequestUrl = eiRequestsControllerUtils.getEIRequestURL(request);
        HttpGet outgoingRequest = new HttpGet(eiRequestUrl);

        outgoingRequest = (HttpGet) addHeadersToRequest(outgoingRequest, request);

        return getResponse(outgoingRequest, request);
    }

    /**
     * Bridge all EI Http Requests with POST method.
     */
    @CrossOrigin
    @RequestMapping(value = { "/subscriptions", "/rules/rule-check/aggregation", "/query" }, method = RequestMethod.POST)
    public ResponseEntity<String> postRequests(Model model, HttpServletRequest incomingRequest) {
        String eiRequestUrl = eiRequestsControllerUtils.getEIRequestURL(incomingRequest);
        String requestBody = "";

        try {
        	// Replaces \r with nothing in case system is run on windows \r may disturb tests. \r does not affect EI functionality.
            requestBody = incomingRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator())).replaceAll("(\\r)", "");
        } catch (IOException e) {
            LOG.error("Forward Request Errors: " + e);
        }

        LOG.debug("Input Request JSON Content to be forwarded:\n" + requestBody);

        HttpEntity inputReqJsonEntity = new ByteArrayEntity(requestBody.getBytes());

        HttpPost outgoingRequest = new HttpPost(eiRequestUrl);
        outgoingRequest.setEntity(inputReqJsonEntity);

        outgoingRequest = (HttpPost) addHeadersToRequest(outgoingRequest, incomingRequest);
        outgoingRequest.setHeader("Content-type", "application/json");

        return getResponse(outgoingRequest, incomingRequest);
    }

    /**
     * Bridge all EI Http Requests with PUT method. E.g. Making Update
     * Subscription Request.
     */
    @CrossOrigin
    @RequestMapping(value = "/subscriptions", method = RequestMethod.PUT)
    public ResponseEntity<String> putRequests(Model model, HttpServletRequest incomingRequest) {
        String eiRequestUrl = eiRequestsControllerUtils.getEIRequestURL(incomingRequest);
        String requestBody = "";

        try {
            requestBody = incomingRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator())).replaceAll("(\\r)", "");
        } catch (IOException e) {
            LOG.error("Forward Request Errors: " + e);
        }

        LOG.debug("Input Request JSON Content to be forwarded:\n" + requestBody);

        HttpEntity inputReqJsonEntity = new ByteArrayEntity(requestBody.getBytes());

        HttpPut outgoingRequest = new HttpPut(eiRequestUrl);
        outgoingRequest.setEntity(inputReqJsonEntity);

        outgoingRequest = (HttpPut) addHeadersToRequest(outgoingRequest, incomingRequest);
        outgoingRequest.setHeader("Content-type", "application/json");

        return getResponse(outgoingRequest, incomingRequest);
    }

    /**
     * Bridge all EI Http Requests with DELETE method. Used for DELETE
     * subscriptions.
     */
    @CrossOrigin
    @RequestMapping(value = "/subscriptions/*", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteRequests(Model model, HttpServletRequest incomingRequest) {
        String eiRequestUrl = eiRequestsControllerUtils.getEIRequestURL(incomingRequest);

        HttpDelete outgoingRequest = new HttpDelete(eiRequestUrl);

        outgoingRequest = (HttpDelete) addHeadersToRequest(outgoingRequest, incomingRequest);

        return getResponse(outgoingRequest, incomingRequest);
    }

    private ResponseEntity<String> getResponse(HttpRequestBase outgoingRequest, HttpServletRequest incomingRequest) {
        HttpHeaders headers = new HttpHeaders();
        String responseBody = "[]";
        int statusCode = HttpStatus.PROCESSING.value();

        String url = outgoingRequest.getURI().toString();
        LOG.debug("Forwarding request to: " + url);
        try (CloseableHttpResponse eiResponse = HttpClientBuilder.create().build().execute(outgoingRequest)) {
            if(eiResponse.getEntity() != null) {
                responseBody = StringUtils.defaultIfBlank(EntityUtils.toString(eiResponse.getEntity(), "utf-8"), "[]");
            }

            headers = getHeadersFromResponse(headers, eiResponse, incomingRequest);
            statusCode = eiResponse.getStatusLine().getStatusCode();

            LOG.debug("EI Http response status code: " + statusCode
                    + "\nEI Received response body:\n" + responseBody
                    + "\nForwarding response back to EI Frontend WebUI.");
        } catch (IOException e) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            responseBody = "{\"statusCode\": " + statusCode + ", \"error\": \"Forward Request Error: " + String.valueOf(e) + "\"}";
            LOG.error("Forward Request Errors: " + e);
        }

        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(responseBody, headers, HttpStatus.valueOf(statusCode));
    }

    private HttpHeaders getHeadersFromResponse(HttpHeaders headers, CloseableHttpResponse eiResponse, HttpServletRequest incomingRequest) {
        List<String> headerNameList = new ArrayList<String>();
        List<String> notCopiedHeaderNameList = new ArrayList<>();

        for (Header header : eiResponse.getAllHeaders()) {
            if (header.getName().equalsIgnoreCase(X_AUTH_TOKEN)) {
                LOG.debug("Adding '" + X_AUTH_TOKEN + "' to current session.");
                incomingRequest.getSession().setAttribute(X_AUTH_TOKEN, header.getValue());
            }

            boolean headerShouldBeCopied = HEADERS_TO_COPY.contains(header.getName().toLowerCase());
            if (headerShouldBeCopied) {
                headerNameList.add(header.getName());
                headers.add(header.getName(), header.getValue());
            } else {
                notCopiedHeaderNameList.add(header.getName());
            }
        }

        LOG.debug("Headers processed returning response: "+
                "\nHeaders copied to the response: " + headerNameList.toString() +
                "\nHeaders not copied to the response: " + notCopiedHeaderNameList.toString());
        return headers;
    }

    private HttpRequestBase addHeadersToRequest(HttpRequestBase outgoingRequest, HttpServletRequest incomingRequest) {
        Enumeration<String> headerNames = incomingRequest.getHeaderNames();
        List<String> headerNameList = new ArrayList<>();
        List<String> notCopiedHeaderNameList = new ArrayList<>();

        while(headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            boolean headerShouldBeCopied = HEADERS_TO_COPY.contains(headerName.toLowerCase());
            if (headerShouldBeCopied) {
                outgoingRequest.addHeader(headerName, incomingRequest.getHeader(headerName));
                headerNameList.add(headerName);
            } else {
                notCopiedHeaderNameList.add(headerName);
            }
        }

        if (incomingRequest.getSession().getAttribute(X_AUTH_TOKEN) != null) {
            LOG.debug("Adding '" + X_AUTH_TOKEN + "' to the request header.");
            outgoingRequest.addHeader(X_AUTH_TOKEN, incomingRequest.getSession().getAttribute(X_AUTH_TOKEN).toString());

            boolean userLoggingOut = incomingRequest.getRequestURL().toString().contains("logout");
            if (userLoggingOut) {
                LOG.debug("Removing '" + X_AUTH_TOKEN + "' from current session.");
                incomingRequest.getSession().setAttribute(X_AUTH_TOKEN, null);
            }
        }

        LOG.debug("Headers processed before making request: "+
                "\nHeaders copied to the request: " + headerNameList.toString() +
                "\nHeaders not copied to the request: " + notCopiedHeaderNameList.toString());

        return outgoingRequest;
    }
}
