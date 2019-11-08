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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

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

import com.ericsson.ei.frontend.model.BackendInstance;
import com.ericsson.ei.frontend.utils.BackendInstancesHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BackendInformationControllerTest {

    private static final String BACKEND_INSTANCES_FILE_PATH = "src/test/resources/backendInstances/backendInstances.json";

    private static final String PATH_FOR_BACK_END_INFORMATION = "/backends";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BackendInstancesHandler utils;

    private JsonArray instances;
    private List<BackendInstance> information;

    @Before
    public void before() throws Exception {
        instances = new JsonParser().parse(new FileReader(BACKEND_INSTANCES_FILE_PATH)).getAsJsonArray();
        information = new ArrayList<>();
        for(JsonElement element : instances) {
            information.add(new ObjectMapper().readValue(element.toString(), BackendInstance.class));
        }
    }

    @Test
    public void testGetInstances() throws Exception {
        when(utils.getBackendInstancesAsJsonArray()).thenReturn(instances);
        mockMvc.perform(MockMvcRequestBuilders.get(PATH_FOR_BACK_END_INFORMATION)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().string(instances.toString()))
            .andReturn();
    }
}
