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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownServiceException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.apache.http.impl.client.HttpClients;


@RestController
@ConfigurationProperties(prefix="ei")
//@RequestMapping(value = "")
public class EIRequestsController {

    private static final Logger LOG = LoggerFactory.getLogger(EIRequestsController.class);
	
	private String backendServerHost;
    private int backendServerPort;

    private static final String APPLICATION_JSON = "application/json";

    // Backend host and port (Getter & Setters), application.properties -> greeting.xxx
    public String getBackendServerHost() {
        return backendServerHost;
    }

    public void setBackendServerHost(String backendServerHost) {
        this.backendServerHost = backendServerHost;
    }

    public int getBackendServerPort() {
        return backendServerPort;
    }

    public void setBackendServerPort(int backendServerPort) {
        this.backendServerPort = backendServerPort;
    }
    
    public String getEIBackendSubscriptionAddress() {
    	return "http://" + getBackendServerHost() + ":" + getBackendServerPort();
	}

    
    /**
     * Bridge all EI Http Requests with GET method. Used for fetching Subscription by id or all subscriptions and EI Env Info.
     * 
     */
    @CrossOrigin
    @RequestMapping(value = {"/subscriptions", "/subscriptions/*", "/information" }, method = RequestMethod.GET)
    public ResponseEntity<String> getRequests(Model model, HttpServletRequest request) {
    	String eiBackendAddressSuffix = request.getServletPath();
    	String newRequestUrl = getEIBackendSubscriptionAddress() + eiBackendAddressSuffix;
    	LOG.info("Got HTTP Request with method GET.\nUrlSuffix: " + eiBackendAddressSuffix +
    			"\nForwarding Request to EI Backend with url: " + newRequestUrl);


    	HttpClient client = HttpClients.createDefault();
    	HttpGet eiRequest = new HttpGet(newRequestUrl);

    	String JsonContent = "";
    	HttpResponse eiResponse = null;
    	try {
    		eiResponse = client.execute(eiRequest);

    		InputStream inStream = eiResponse.getEntity().getContent();
    		BufferedReader bufReader =  new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
    		for (String line = bufReader.readLine(); line != null; line = bufReader.readLine()) {
    			JsonContent += line;
    		}
    		bufReader.close();
    		inStream.close();
    	}
    	catch (IOException e) {
    		LOG.error("Forward Request Errors: " + e);
    	}

    	LOG.debug("Http Status Code: " + eiResponse.getStatusLine().getStatusCode());
    	LOG.debug("Recevied JsonContent:\n" + JsonContent);
    	
    	if (JsonContent.isEmpty()) {
    		JsonContent="[]";
    	}
    	
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

    	ResponseEntity<String> responseEntity = new ResponseEntity<>(JsonContent,
    			headers,
    			HttpStatus.valueOf(eiResponse.getStatusLine().getStatusCode()));
    	return responseEntity;
    }


	
    /**
     * Bridge all EI Http Requests with POST method. E.g. Making Create Subscription Request.
     * 
     */
	@CrossOrigin
	@RequestMapping(value = "/subscriptions", method = RequestMethod.POST)
	public ResponseEntity<String> postRequests(Model model, HttpServletRequest request) {
		
    	String eiBackendAddressSuffix = request.getServletPath();
    	String newRequestUrl = getEIBackendSubscriptionAddress() + eiBackendAddressSuffix;
    	LOG.info("Got HTTP Request with method POST.\nUrlSuffix: " + eiBackendAddressSuffix +
    			"\nForwarding Request to EI Backend with url: " + newRequestUrl);
    	
    	String inputReqJsonContent = "";
    	try {
    		BufferedReader inputBufReader =  new BufferedReader(request.getReader());
    		for (String line = inputBufReader.readLine(); line != null; line = inputBufReader.readLine()) {
    			inputReqJsonContent += line;
    		}
    		inputBufReader.close();
    	}
    	catch (IOException e) {
    		LOG.error("Forward Request Errors: " + e);
    	}

    	LOG.debug("Input Request JSON Content to be forwarded:\n" + inputReqJsonContent);
    	HttpEntity inputReqJsonEntity = new ByteArrayEntity(inputReqJsonContent.getBytes());
    	
    	
    	HttpClient client = HttpClients.createDefault();
    	HttpPost eiRequest = new HttpPost(newRequestUrl);
    	eiRequest.setEntity(inputReqJsonEntity);
    	eiRequest.setHeader("Content-type", "application/json");

    	
    	String JsonContent = "";
    	HttpResponse eiResponse = null;
    	try {
    		eiResponse = client.execute(eiRequest);

    		InputStream inStream = eiResponse.getEntity().getContent();
    		BufferedReader bufReader =  new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
    		for (String line = bufReader.readLine(); line != null; line = bufReader.readLine()) {
    			JsonContent += line;
    		}
    		bufReader.close();
    		inStream.close();
    	}
    	catch (IOException e) {
    		LOG.error("Forward Request Errors: " + e);
    	}

    	LOG.debug("Http Status Code: " + eiResponse.getStatusLine().getStatusCode());
    	LOG.debug("Recevied JsonContent: \n" + JsonContent);
    	
    	if (JsonContent.isEmpty()) {
    		JsonContent="[]";
    	}

    	
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<String> responseEntity = new ResponseEntity<>(JsonContent,
				headers,
    			HttpStatus.valueOf(eiResponse.getStatusLine().getStatusCode()));
		 return responseEntity;
	}

	/**
     * Bridge all EI Http Requests with PUT method. E.g. Making Update Subscription Request.
     * 
     */
	@CrossOrigin
	@RequestMapping(value = "/subscriptions", method = RequestMethod.PUT)
	public ResponseEntity<String> putRequests(Model model, HttpServletRequest request) {
		
    	String eiBackendAddressSuffix = request.getServletPath();
    	String newRequestUrl = getEIBackendSubscriptionAddress() + eiBackendAddressSuffix;
    	LOG.info("Got HTTP Request with method PUT.\nUrlSuffix: " + eiBackendAddressSuffix +
    			"\nForwarding Request to EI Backend with url: " + newRequestUrl);
    	
    	String inputReqJsonContent = "";
    	try {
    		BufferedReader inputBufReader =  new BufferedReader(request.getReader());
    		for (String line = inputBufReader.readLine(); line != null; line = inputBufReader.readLine()) {
    			inputReqJsonContent += line;
    		}
    		inputBufReader.close();
    	}
    	catch (IOException e) {
    		LOG.error("Forward Request Errors: " + e);
    	}

    	LOG.debug("Input Request JSON Content to be forwarded:\n" + inputReqJsonContent);
    	HttpEntity inputReqJsonEntity = new ByteArrayEntity(inputReqJsonContent.getBytes());
    	
    	
    	HttpClient client = HttpClients.createDefault();
    	HttpPut eiRequest = new HttpPut(newRequestUrl);
    	eiRequest.setEntity(inputReqJsonEntity);
    	eiRequest.setHeader("Content-type", "application/json");

    	
    	String JsonContent = "";
    	HttpResponse eiResponse = null;
    	try {
    		eiResponse = client.execute(eiRequest);

    		InputStream inStream = eiResponse.getEntity().getContent();
    		BufferedReader bufReader =  new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
    		for (String line = bufReader.readLine(); line != null; line = bufReader.readLine()) {
    			JsonContent += line;
    		}
    		bufReader.close();
    		inStream.close();
    	}
    	catch (IOException e) {
    		LOG.error("Forward Request Errors: " + e);
    	}

    	LOG.debug("Http Status Code: " + eiResponse.getStatusLine().getStatusCode());
    	LOG.debug("Recevied JsonContent:\n" + JsonContent);
		
    	if (JsonContent.isEmpty()) {
    		JsonContent="[]";
    	}

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
		ResponseEntity<String> responseEntity = new ResponseEntity<String>(JsonContent,
				headers,
				HttpStatus.valueOf(eiResponse.getStatusLine().getStatusCode()));

		 return responseEntity;
	}


    /**
     * Bridge all EI Http Requests with DELETE method. Used for DELETE subscriptions.
     * 
     */
    @CrossOrigin
    @RequestMapping(value = "/subscriptions/*", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteRequests(Model model, HttpServletRequest request) {
    	String eiBackendAddressSuffix = request.getServletPath();
    	String newRequestUrl = getEIBackendSubscriptionAddress() + eiBackendAddressSuffix;
    	LOG.info("Got HTTP Request with method DELETE.\nUrlSuffix: " + eiBackendAddressSuffix +
    			"\nForwarding Request to EI Backend with url: " + newRequestUrl);

    	HttpClient client = HttpClients.createDefault();
    	HttpDelete eiRequest = new HttpDelete(newRequestUrl);

    	String JsonContent = "";
    	HttpResponse eiResponse = null;
    	try {
    		eiResponse = client.execute(eiRequest);

    		InputStream inStream = eiResponse.getEntity().getContent();
    		BufferedReader bufReader =  new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
    		for (String line = bufReader.readLine(); line != null; line = bufReader.readLine()) {
    			JsonContent += line;
    		}
    		bufReader.close();
    		inStream.close();
    	}
    	catch (IOException e) {
    		LOG.error("Forward Request Errors: " + e);
    	}

    	LOG.debug("Http Status Code: " + eiResponse.getStatusLine().getStatusCode());
    	LOG.debug("Recevied JsonContent:\n" + JsonContent);
    	
    	if (JsonContent.isEmpty()) {
    		JsonContent="[]";
    	}
    	
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    	
        ResponseEntity<String> responseEntity = new ResponseEntity<>(JsonContent,
        		headers,
    			HttpStatus.valueOf(eiResponse.getStatusLine().getStatusCode()));
    	return responseEntity;
    }
	
}