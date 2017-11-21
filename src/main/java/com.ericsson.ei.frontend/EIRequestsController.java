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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
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


//import reactor.core.publisher.Mono;


@RestController
@ConfigurationProperties(prefix="ei")
//@RequestMapping(value = "")
public class EIRequestsController {

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
    public ResponseEntity<?> getRequests(Model model, HttpServletRequest request) {
    	System.out.println("Request with method GET.");
    	String eiBackendAddressSuffix = request.getServletPath();
    	System.out.println("URL suffix received: " + eiBackendAddressSuffix);
    	String newRequestUrl = getEIBackendSubscriptionAddress() + eiBackendAddressSuffix;
    	System.out.println("New EI request URL: " + newRequestUrl);

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
    		System.out.println("ERROR CLIENT: " + e);
    	}

    	System.out.println("HTTP STATUS: " + eiResponse.getStatusLine().getStatusCode());
    	System.out.println(JsonContent);

    	ResponseEntity<String> responseEntity = new ResponseEntity<>(JsonContent,
    			HttpStatus.valueOf(eiResponse.getStatusLine().getStatusCode()));
    	return responseEntity;
    }


	
    /**
     * Bridge all EI Http Requests with POST method. E.g. Making Create Subscription Request.
     * 
     */
	@CrossOrigin
	@RequestMapping(value = "/subscriptions", method = RequestMethod.POST)
	public ResponseEntity<?> createSubscription(Model model, HttpServletRequest request) {
		
    	System.out.println("Request with method POST.");
    	String eiBackendAddressSuffix = request.getServletPath();
    	System.out.println("URL suffix received: " + eiBackendAddressSuffix);
    	String newRequestUrl = getEIBackendSubscriptionAddress() + eiBackendAddressSuffix;
    	System.out.println("New EI request URL: " + newRequestUrl);
    	
    	String inputReqJsonContent = "";
    	try {
    		BufferedReader inputBufReader =  new BufferedReader(request.getReader());
    		for (String line = inputBufReader.readLine(); line != null; line = inputBufReader.readLine()) {
    			inputReqJsonContent += line;
    		}
    		inputBufReader.close();
    	}
    	catch (IOException e) {
    		System.out.println("ERROR CLIENT: " + e);
    	}

    	System.out.println("Request JSON Content: " + inputReqJsonContent);
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
    		System.out.println("ERROR CLIENT: " + e);
    	}

    	System.out.println("HTTP STATUS: " + eiResponse.getStatusLine().getStatusCode());
    	System.out.println(JsonContent);
		
		
		ResponseEntity<String> responseEntity = new ResponseEntity<>("[]",
    			HttpStatus.valueOf(eiResponse.getStatusLine().getStatusCode()));
		 return responseEntity;
	}
//	
//    /**
//     * Modify an existing Subscription.
//     * 
//     */
//	@CrossOrigin
//	@RequestMapping(value = "", method = RequestMethod.PUT)
//	public ResponseEntity<?> updateSubscriptions(HttpServletResponse response,
//			HttpServletRequest request) {
//		 return new ResponseEntity<>(response, HttpStatus.OK);
//				 //"redirect:" + eiBackendServiceAddress;
//	}
//	


//    /**
//     * Removes the subscription from the database.
//     * 
//     */
//	@CrossOrigin
//	@RequestMapping(value = "/{subscriptionName}", method = RequestMethod.DELETE)
//	public ResponseEntity<?> deleteSubscriptionById(@PathVariable String subscriptionName,
//			HttpServletResponse response,
//			HttpServletRequest request) {
//		
//		System.out.println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
//		return new ResponseEntity<>(response, HttpStatus.OK);
//				//"forward:" + eiBackendServiceAddress + "/{" + subscriptionName + "}";
//	}
//	
	
}