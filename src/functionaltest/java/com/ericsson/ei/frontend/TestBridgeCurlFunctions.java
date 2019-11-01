package com.ericsson.ei.frontend;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.io.IOException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;

public class TestBridgeCurlFunctions extends TestBaseClass {
    private static MockServerClient mockClient1;
    private static MockServerClient mockClient2;
    private static ClientAndServer mockServer1;
    private static ClientAndServer mockServer2;

    private static final String BASE_URL = "localhost";
    private static final String SUBSCRIPTION_ENDPOINT = "/subscriptions";
    private static final String BACKEND_PARAM = "backendurl";

    private static String mockServer2Url;

    @Before
    public void before() throws Exception {
        int serverPort = mockServer1.getLocalPort();

        setBackendInstance("test", BASE_URL, serverPort, "", true);
        setupMockEndpoints();
    }

    /**
     * This test does a normal request to the bridge and ensures it uses the default specified back end.
     *
     * @throws Exception
     */
    @Test
    public void testHandleSubscriptionsDefaultBackEnd() throws Exception {
        mockMvc.perform(put(SUBSCRIPTION_ENDPOINT).servletPath(SUBSCRIPTION_ENDPOINT));
        mockClient1.verify(request().withMethod("PUT").withPath(SUBSCRIPTION_ENDPOINT));

        mockMvc.perform(post(SUBSCRIPTION_ENDPOINT).servletPath(SUBSCRIPTION_ENDPOINT));
        mockClient1.verify(request().withMethod("POST").withPath(SUBSCRIPTION_ENDPOINT));

        mockMvc.perform(get(SUBSCRIPTION_ENDPOINT).servletPath(SUBSCRIPTION_ENDPOINT));
        mockClient1.verify(request().withMethod("GET").withPath(SUBSCRIPTION_ENDPOINT));

        mockClient2.verifyZeroInteractions();
    }

    /**
     * This test ensures that a back end specified by a URL parameter is overriding the default back end and uses the input one instead.
     *
     * @throws Exception
     */
    @Test
    public void testHandleSubscriptionsUserSpecifiedBackend() throws Exception {
        URIBuilder builder = new URIBuilder();
        builder.setParameter(BACKEND_PARAM, mockServer2Url).setPath(SUBSCRIPTION_ENDPOINT).setHost(BASE_URL).setPort(testServerPort)
                .setScheme("http");

        /**
         * Since MockMvc has problem including real parameters in a request we use a real http request and sends this request to the bridge.
         */
        HttpClientBuilder.create().build().execute(new HttpGet(builder.toString()));
        mockClient2.verify(request().withMethod("GET").withPath(SUBSCRIPTION_ENDPOINT));

        HttpClientBuilder.create().build().execute(new HttpPost(builder.toString()));
        mockClient2.verify(request().withMethod("POST").withPath(SUBSCRIPTION_ENDPOINT));

        HttpClientBuilder.create().build().execute(new HttpPut(builder.toString()));
        mockClient2.verify(request().withMethod("PUT").withPath(SUBSCRIPTION_ENDPOINT));

        mockClient1.verifyZeroInteractions();
    }

    @After
    public void after() throws IOException {
        mockClient1.clear(request());
        mockClient2.clear(request());
    }

    @BeforeClass
    public static void setUpMocks() throws IOException {
        mockServer1 = startClientAndServer();
        mockServer2 = startClientAndServer();

        mockClient1 = new MockServerClient(BASE_URL, mockServer1.getLocalPort());
        mockClient2 = new MockServerClient(BASE_URL, mockServer2.getLocalPort());

        mockServer2Url = "HTTP://" + BASE_URL + ":" + mockServer2.getLocalPort();
    }

    @AfterClass
    public static void tearDownMocks() throws IOException {
        mockClient1.stop();
        mockClient2.stop();
    }

    private void setupMockEndpoints() {
        mockClient1.when(request().withMethod("GET")).respond(response().withStatusCode(200));
        mockClient1.when(request().withMethod("POST")).respond(response().withStatusCode(200));
        mockClient1.when(request().withMethod("PUT")).respond(response().withStatusCode(200));

        mockClient2.when(request().withMethod("GET")).respond(response().withStatusCode(200));
        mockClient2.when(request().withMethod("POST")).respond(response().withStatusCode(200));
        mockClient2.when(request().withMethod("PUT")).respond(response().withStatusCode(200));
    }
}