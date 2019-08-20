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

import java.io.FileReader;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ericsson.ei.frontend.exceptions.EiBackendInstancesException;
import com.ericsson.ei.frontend.model.BackendInstance;
import com.ericsson.ei.frontend.utils.BackEndInstancesHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class BackendInstancesUtilsTest {

    private static final String BACKEND_INSTANCE_FILE_PATH = "src/test/resources/backendInstances/backendInstance.json";
    private static final String BACKEND_INSTANCES_FILE_PATH = "src/test/resources/backendInstances/backendInstances.json";

    @Autowired
    private BackEndInstancesHandler utils;

    private JsonObject instance;
    private JsonArray instances;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void before() throws IOException {
        instance = new JsonParser().parse(new FileReader(BACKEND_INSTANCE_FILE_PATH)).getAsJsonObject();
        instances = new JsonParser().parse(new FileReader(BACKEND_INSTANCES_FILE_PATH)).getAsJsonArray();
    }

    @Test
    public void testGetBackendInstance() throws EiBackendInstancesException {
        // Test when name was found.
        utils.setBackendInstances(instances);
        String nameToGet = instances.get(0).getAsJsonObject().get("name").getAsString();
        BackendInstance result = utils.getBackendInstance(nameToGet);

        assertEquals("Expected instance data:\n" + instances.get(0).getAsJsonObject() + "\nBut got data:\n"
                + result.getAsJsonObject(), instances.get(0).getAsJsonObject(), result.getAsJsonObject());

        // No back end data exist
        exceptionRule.expect(EiBackendInstancesException.class);
        utils.setBackendInstances(new JsonArray());
        BackendInstance result2 = utils.getBackendInstance("does not exist");
    }

    @Test
    public void testGetBackEndsAsJsonArray() {
        utils.setBackendInstances(instances);
        JsonArray result = utils.getBackendInstancesAsJsonArray();
        assertEquals(instances, result);
    }
}
