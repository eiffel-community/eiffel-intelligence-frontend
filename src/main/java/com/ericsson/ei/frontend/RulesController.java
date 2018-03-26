package com.ericsson.ei.frontend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rules")
public class RulesController {

    private static final Logger LOG = LoggerFactory.getLogger(RulesController.class);

    @Value("${ei.backendServerHost}")
    private String backendServerHost;

    @Value("${ei.backendServerPort}")
    private int backendServerPort;

    @Value("${ei.backendContextPath}")
    private String backendContextPath;

    @Value("${ei.useSecureHttp}")
    private boolean useSecureHttp;

    @CrossOrigin
    @RequestMapping(value = "/aggregate", method = RequestMethod.POST)
    public ResponseEntity<String> postRequests(HttpServletRequest request) {

        String eiBackendAddressSuffix = request.getServletPath();
        String newRequestUrl = getEIBackendRulesRestPointAddress() + eiBackendAddressSuffix;
        LOG.info("Got HTTP Request with method POST.\nUrlSuffix: " + eiBackendAddressSuffix
                + "\nForwarding Request to EI Backend with url: " + newRequestUrl);

        HttpClient client = HttpClients.createDefault();
        HttpPost eiRequest = new HttpPost(newRequestUrl);

        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("listRulesJson", request.getParameter("listRulesJson")));
        postParameters.add(new BasicNameValuePair("listEventsJson", request.getParameter("listEventsJson")));

        try {
            eiRequest.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
        } catch (UnsupportedEncodingException e1) {

        }
        eiRequest.setHeader("Content-type", "application/json");

        String jsonContent = "";
        HttpResponse eiResponse = null;
        try {
            eiResponse = client.execute(eiRequest);

            InputStream inStream = eiResponse.getEntity().getContent();
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
            for (String line = bufReader.readLine(); line != null; line = bufReader.readLine()) {
                jsonContent += line;
            }
            bufReader.close();
            inStream.close();
        } catch (IOException e) {
            LOG.error("Forward Request Errors: " + e);
        }

        LOG.info("EI Http Reponse Status Code: " + eiResponse.getStatusLine().getStatusCode()
                + "\nEI Recevied jsonContent:\n" + jsonContent + "\nForwarding response back to EI Frontend WebUI.");

        if (jsonContent.isEmpty()) {
            jsonContent = "[]";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonContent, headers,
                HttpStatus.valueOf(eiResponse.getStatusLine().getStatusCode()));
        return responseEntity;
    }

    private String getEIBackendRulesRestPointAddress() {
        String httpMethod = "http";
        if (useSecureHttp) {
            httpMethod = "https";
        }

        if (backendContextPath != null && !backendContextPath.isEmpty()) {
            return httpMethod + "://" + backendServerHost + ":" + backendServerPort + "/" + backendContextPath;
        }
        return httpMethod + "://" + backendServerHost + ":" + backendServerPort;
    }
}
