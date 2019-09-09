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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ericsson.ei.frontend.model.BackendInstance;
import com.ericsson.ei.frontend.utils.BackendInformationControllerUtils;
import com.ericsson.ei.frontend.utils.BackendInstancesHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BackendInformationControllerUtilsTest {

    private static final String BACKEND_INSTANCE_FILE_PATH = "src/test/resources/backendInstances/backendInstance.json";
    private static final String BACKEND_INSTANCES_FILE_PATH = "src/test/resources/backendInstances/backendInstances.json";

    @MockBean
    private BackendInstancesHandler backEndInstancesUtils;

    @Autowired
    private BackendInformationControllerUtils backendInfoContrUtils;

    private JsonObject instance;
    private JsonArray instances;
    private JsonArray instancesWithActive;
    private JsonArray instancesWithNotDefaultActive;
    private List<BackendInstance> information;

    private HttpServletRequest mockedRequest;
    private HttpSession mockedSession;
    private BufferedReader mockedReader;
    private Stream<String> stream;

    @Before
    public void before() throws Exception {
        instance = new JsonParser().parse(new FileReader(BACKEND_INSTANCE_FILE_PATH)).getAsJsonObject();
        instances = new JsonParser().parse(new FileReader(BACKEND_INSTANCES_FILE_PATH)).getAsJsonArray();

        mockedRequest = Mockito.mock(HttpServletRequest.class);
        mockedSession = Mockito.mock(HttpSession.class);
        mockedReader = Mockito.mock(BufferedReader.class);
        stream = Mockito.mock(Stream.class);

        when(mockedRequest.getSession()).thenReturn(mockedSession);
        when(mockedRequest.getReader()).thenReturn(mockedReader);
        when(mockedReader.lines()).thenReturn(stream);

        when(mockedRequest.getMethod()).thenReturn("Test");
        when(backEndInstancesUtils.getBackendInstancesAsJsonArray()).thenReturn(instances);
    }

    @Test
    public void testHandleRequestForInstances() throws Exception {
        ResponseEntity<String> response;
        ResponseEntity<String> expectedResponse;

        expectedResponse = createExpectedResponse(instances.toString(), HttpStatus.OK);
        response = backendInfoContrUtils.handleRequestForInstances(mockedRequest);
        assertEquals(expectedResponse, response);
    }

    @Test
    public void testHandleRequestToSwitchBackEnd() throws Exception {
        ResponseEntity<String> response;
        ResponseEntity<String> expectedResponse;

        // Test name is given.
        when(stream.collect(any())).thenReturn("TestName");
        expectedResponse = createExpectedResponse(
                "{\"message\": \"Backend instance with name 'TestName' was selected.\"}".toString(), HttpStatus.OK);
        response = backendInfoContrUtils.handleRequestToSwitchBackEnd(mockedRequest);
        assertEquals(expectedResponse, response);
    }

    private ResponseEntity<String> createExpectedResponse(String response, HttpStatus status) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setLocation(URI.create("/"));
        ResponseEntity<String> expectedResponse = new ResponseEntity<>(response, httpHeaders, status);
        return expectedResponse;
    }

}
