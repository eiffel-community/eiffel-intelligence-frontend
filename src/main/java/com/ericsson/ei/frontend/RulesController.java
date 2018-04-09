package com.ericsson.ei.frontend;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
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
    @RequestMapping(value = "/rule-check/aggregation", method = RequestMethod.POST)
    public ResponseEntity<String> postRequests(HttpServletRequest request) {

        String eiBackendAddressSuffix = request.getServletPath();
        String newRequestUrl = getEIBackendRulesRestPointAddress() + eiBackendAddressSuffix;
        LOG.info("Got HTTP Request with method POST.\nUrlSuffix: " + eiBackendAddressSuffix
                + "\nForwarding Request to EI Backend with url: " + newRequestUrl);
        String inputReqJsonContent = "";
        try {
            BufferedReader inputBufReader = new BufferedReader(request.getReader());
            for (String line = inputBufReader.readLine(); line != null; line = inputBufReader.readLine()) {
                inputReqJsonContent += line;
            }
            inputBufReader.close();

            LOG.debug("Input Request JSON Content to be forwarded:\n" + inputReqJsonContent);
            StringEntity entity = new StringEntity(inputReqJsonContent);
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

            HttpClient client = HttpClients.createDefault();
            HttpPost eiRequest = new HttpPost(newRequestUrl);
            eiRequest.setEntity(entity);
            eiRequest.setHeader("Content-type", "application/json");

            String jsonContent = "";
            HttpResponse eiResponse = null;
            eiResponse = client.execute(eiRequest);

            InputStream inStream = eiResponse.getEntity().getContent();
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
            for (String line = bufReader.readLine(); line != null; line = bufReader.readLine()) {
                jsonContent += line;
            }
            bufReader.close();
            inStream.close();

            LOG.info("EI Http Reponse Status Code: " + eiResponse.getStatusLine().getStatusCode()
                    + "\nEI Recevied jsonContent:\n" + jsonContent
                    + "\nForwarding response back to EI Frontend WebUI.");

            if (jsonContent.isEmpty()) {
                jsonContent = "[]";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonContent, headers,
                    HttpStatus.valueOf(eiResponse.getStatusLine().getStatusCode()));
            return responseEntity;
        } catch (Exception e) {
            LOG.error("Forward Request Errors: " + e);
            ResponseEntity<String> responseEntity = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
            return responseEntity;
        }

        
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
