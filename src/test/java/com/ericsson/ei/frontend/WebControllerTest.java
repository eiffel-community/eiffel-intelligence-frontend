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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.ericsson.ei.frontend.exceptions.EiBackendInstancesException;
import com.ericsson.ei.frontend.utils.WebControllerUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WebControllerTest {

    private static String FRONT_END_SERVICE_URL = "http://localhost:9090/somePath";
    private static String BACK_END_SERVICE_URL = "http://localhost:9090/somePath";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WebControllerUtils controllerUtils;

    @Before
    public void beforeClass() throws EiBackendInstancesException {
        when(controllerUtils.getFrontEndServiceUrl()).thenReturn(FRONT_END_SERVICE_URL);
        when(controllerUtils.getBackEndServiceUrl(any())).thenReturn(BACK_END_SERVICE_URL);
    }

    @Test
    public void testIndex() throws Exception {
        testGet("/");
    }

    @Test
    public void testSubscriptions() throws Exception {
        testGet("/subscriptions.html");
    }

    @Test
    public void testTestRules() throws Exception {
        testGet("/test-rules.html");
    }

    @Test
    public void testInfo() throws Exception {
        testGet("/information.html");
    }

    @Test
    public void testRules() throws Exception {
        testGet("/rules.html");
    }

    @Test
    public void testLogin() throws Exception {
        testGet("/login.html");
    }

    @Test
    public void testSwitchBackend() throws Exception {
        testGet("/switch-backend.html");
    }

    private void testGet(String path) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(path)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(model().attribute("frontendServiceUrl", "http://localhost:9090/somePath"))
            .andReturn();
    }

}
