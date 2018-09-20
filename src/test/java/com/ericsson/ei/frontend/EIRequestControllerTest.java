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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.ericsson.ei.frontend.model.BackEndInformation;
import com.ericsson.ei.frontend.utils.BackEndInstanceFileUtils;
import com.ericsson.ei.frontend.utils.BackEndInstancesUtils;
import com.google.gson.JsonParser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EIRequestControllerTest {

    private static final String SUBSCRIPTIONS_ENDPOINT = "/subscriptions";
    private static final String SUBSCRIPTIONS_ONE_ENDPOINT = "/subscriptions/Subscription_1";
    private static final String INFORMATION_ENDPOINT = "/information";
    private static final String DOWNLOAD_RULES_TEMPLATE_ENDPOINT = "/download/rulesTemplate";
    private static final String AUTH_ENDPOINT = "/auth";
    private static final String AUTH_LOGIN_ENDPOINT = "/auth/login";

    private static final String SUBSCRIPTIONS_RESPONSE_PATH = "src/test/resources/backendResponses/subscriptionsResponse.json";
    private static final String SUBSCRIPTIONS_ONE_RESPONSE_PATH = "src/test/resources/backendResponses/subscriptionsOneResponse.json";
    private static final String INFORMATION_RESPONSE_PATH = "src/test/resources/backendResponses/informationResponse.json";
    private static final String DOWNLOAD_RULES_TEMPLATE_RESPONSE_PATH = "src/test/resources/backendResponses/downloadRulesTemplateResponse.json";

    private static final String BACKEND_INFO = "src/test/resources/backendResponses/fileToWriteInstances.json";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BackEndInstancesUtils backEndInstanceUtils;

    @Autowired
    private BackEndInstanceFileUtils backEndInstanceFileUtils;

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);

    private MockServerClient mockServerClient;

    @Before
    public void before() {
        backEndInstanceFileUtils.setEiInstancesPath(BACKEND_INFO);
        BackEndInformation backEndInformation = backEndInstanceUtils.getDefaultBackendInformation();
        backEndInformation.setName("test");
        backEndInformation.setHost("localhost");
        backEndInformation.setPort(String.valueOf(mockServerRule.getPort()));
        backEndInformation.setPath("");
        backEndInformation.setUseSecureHttpBackend(false);
        backEndInformation.setDefaultBackend(true);
    }

    @Test
    public void testGetSubscriptions() throws Exception {
        String responseBody = new JsonParser().parse(new FileReader(SUBSCRIPTIONS_RESPONSE_PATH)).toString();
        testGet(SUBSCRIPTIONS_ENDPOINT, responseBody);
    }

    @Test
    public void testGetSubscriptionsOne() throws Exception {
        String responseBody = new JsonParser().parse(new FileReader(SUBSCRIPTIONS_ONE_RESPONSE_PATH)).toString();
        testGet(SUBSCRIPTIONS_ONE_ENDPOINT, responseBody);
    }

    @Test
    public void testGetInformation() throws Exception {
        String responseBody = new JsonParser().parse(new FileReader(INFORMATION_RESPONSE_PATH)).toString();
        testGet(INFORMATION_ENDPOINT, responseBody);
    }

    @Test
    public void testGetDownload() throws Exception {
        String responseBody = new JsonParser().parse(new FileReader(DOWNLOAD_RULES_TEMPLATE_RESPONSE_PATH)).toString();
        testGet(DOWNLOAD_RULES_TEMPLATE_ENDPOINT, responseBody);
    }

    @Test
    public void testGetAuth() throws Exception {
        String responseBody = new JsonParser().parse("{\"security\":false}").toString();
        testGet(AUTH_ENDPOINT, responseBody);
    }

    @Test
    public void testGetAuthLogin() throws Exception {
        String responseBody = new JsonParser().parse("{\"user\":\"currentUser\"}").toString();
        mockServerClient
            .when(HttpRequest.request()
                .withMethod("GET")
                .withPath(AUTH_LOGIN_ENDPOINT)
            )
            .respond(HttpResponse.response()
                .withBody(responseBody)
                .withStatusCode(200)
            );
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_LOGIN_ENDPOINT)
            .servletPath(AUTH_LOGIN_ENDPOINT)
            .header("Authorization", "Basic QWxhZGRpbjpPcGVuU2VzYW1l")
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().string(responseBody))
            .andReturn();
    }

    @Test
    public void testGetWithEmptyResponseBody() throws Exception {
        mockServerClient.when(HttpRequest.request().withMethod("GET").withPath(SUBSCRIPTIONS_ENDPOINT))
                .respond(HttpResponse.response().withStatusCode(200));
        mockMvc.perform(MockMvcRequestBuilders.get(SUBSCRIPTIONS_ENDPOINT).servletPath(SUBSCRIPTIONS_ENDPOINT)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andExpect(content().string("[]"))
                .andReturn();
    }

    @Test
    public void testPostSubscription() throws Exception {
        String requestBody = "[" + new JsonParser().parse(new FileReader(SUBSCRIPTIONS_ONE_RESPONSE_PATH)).toString() + "]";
        String responseBody = new JsonParser().parse("{\"msg\": \"Inserted Successfully\"," + "\"statusCode\": 200}").toString();
        testPost(SUBSCRIPTIONS_ENDPOINT, requestBody, responseBody);
    }

    @Test
    public void testPutSubscription() throws Exception {
        String requestBody = "[" + new JsonParser().parse(new FileReader(SUBSCRIPTIONS_ONE_RESPONSE_PATH)).toString() + "]";
        String responseBody = new JsonParser().parse("{\"msg\": \"Updated Successfully\"," + "\"statusCode\": 200}").toString();
        testPut(SUBSCRIPTIONS_ENDPOINT, requestBody, responseBody);
    }

    @Test
    public void testDeleteSubscription() throws Exception {
        String responseBody = new JsonParser().parse("{\"msg\": \"Deleted Successfully\"," + "\"statusCode\": 200}").toString();
        testDelete(SUBSCRIPTIONS_ONE_ENDPOINT, responseBody);
    }

    private void testGet(String path, String responseBody) throws Exception {
        mockServerClient.when(HttpRequest.request().withMethod("GET").withPath(path))
                .respond(HttpResponse.response().withBody(responseBody).withStatusCode(200));

        mockMvc.perform(MockMvcRequestBuilders.get(path).servletPath(path).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andExpect(content().string(responseBody)).andReturn();
    }

    private void testPost(String path, String requestBody, String responseBody) throws Exception {
        mockServerClient.when(HttpRequest.request().withMethod("POST").withPath(path).withBody(requestBody))
                .respond(HttpResponse.response().withBody(responseBody).withStatusCode(200));

        mockMvc.perform(MockMvcRequestBuilders.post(path).servletPath(path).content(requestBody)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk())
                .andExpect(content().string(responseBody)).andReturn();
    }

    private void testPut(String path, String requestBody, String responseBody) throws Exception {
        mockServerClient.when(HttpRequest.request().withMethod("PUT").withPath(path).withBody(requestBody))
                .respond(HttpResponse.response().withBody(responseBody).withStatusCode(200));

        mockMvc.perform(MockMvcRequestBuilders.put(path).servletPath(path).content(requestBody)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk())
                .andExpect(content().string(responseBody)).andReturn();
    }

    private void testDelete(String path, String responseBody) throws Exception {
        mockServerClient.when(HttpRequest.request().withMethod("DELETE").withPath(path))
                .respond(HttpResponse.response().withBody(responseBody).withStatusCode(200));

        mockMvc.perform(MockMvcRequestBuilders.delete(path).servletPath(path).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andExpect(content().string(responseBody)).andReturn();
    }

    @AfterClass
    public static void afterClass() throws IOException {
        Files.deleteIfExists(Paths.get(BACKEND_INFO));
    }
}
