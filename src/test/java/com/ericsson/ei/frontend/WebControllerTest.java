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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebController controller;

    @Autowired
    private BackEndInformation information;

    @Before
    public void beforeClass() {
        ReflectionTestUtils.setField(controller,"frontendServiceHost", "localhost");
        ReflectionTestUtils.setField(controller,"frontendServicePort", 9090);
        ReflectionTestUtils.setField(controller,"frontendContextPath", "somePath");
        ReflectionTestUtils.setField(information,"https", false);
    }

    @Test
    public void testIndex() throws Exception {
        testGet("/");
    }

    @Test
    public void testSubscription() throws Exception {
        testGet("/subscriptionpage.html");
    }

    @Test
    public void testRules() throws Exception {
        testGet("/testRules.html");
    }

    @Test
    public void testEiInfo() throws Exception {
        testGet("/eiInfo.html");
    }

    @Test
    public void testLogin() throws Exception {
        testGet("/login.html");
    }

    @Test
    public void testAddInstances() throws Exception {
        testGet("/add-instances.html");
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
            .andDo(print())
            .andReturn();
    }

}
