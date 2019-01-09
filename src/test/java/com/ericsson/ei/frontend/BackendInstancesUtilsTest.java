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
import static org.mockito.Mockito.when;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ericsson.ei.frontend.model.BackEndInformation;
import com.ericsson.ei.frontend.utils.BackEndInstanceFileUtils;
import com.ericsson.ei.frontend.utils.BackEndInstancesUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class BackendInstancesUtilsTest {

    private static final String BACKEND_INSTANCE_FILE_PATH = "src/test/resources/backendInstances/backendInstance.json";
    private static final String BACKEND_INSTANCES_FILE_PATH = "src/test/resources/backendInstances/backendInstances.json";

    @Autowired
    private BackEndInstancesUtils utils;

    @MockBean
    private BackEndInstanceFileUtils fileUtils;

    private JsonObject instance;
    private JsonArray instances;

    @Before
    public void before() throws IOException {
        utils.setRunningTests(true);
        instance = new JsonParser().parse(new FileReader(BACKEND_INSTANCE_FILE_PATH)).getAsJsonObject();
        instances = new JsonParser().parse(new FileReader(BACKEND_INSTANCES_FILE_PATH)).getAsJsonArray();

        utils.getDefaultBackendInformation().setHost(null);
    }

    @Test
    public void testHasRequiredJsonKeys() {
        // removing required key port and the json object should not be valid
        instance.remove("port");
        assertEquals(false, utils.hasRequiredJsonKeys(instance));

        // adding the key port again and it should pass
        instance.addProperty("port", 1234);
        assertEquals(true, utils.hasRequiredJsonKeys(instance));
    }

    @Test
    public void testContainsAdditionalKeysTrue() {
        // add extra random key
        instance.addProperty("randomKey", "randomValue");
        assertEquals(true, utils.containsAdditionalKeys(instance));

        // clean up of excess JSON key
        instance.remove("randomKey");
        assertEquals(false, utils.containsAdditionalKeys(instance));
    }

    @Test
    public void testCheckIfInstanceAlreadyExistTrue() {
        boolean result;
        when(fileUtils.getInstancesFromFile()).thenReturn(instances);

        result = utils.checkIfInstanceAlreadyExist(instance);
        assertEquals("Instance should already exist: '" + result + "'.", true, result);

        JsonObject newInstance = instance.getAsJsonObject();
        newInstance.addProperty("host", "newHost");
        newInstance.addProperty("port", newInstance.get("port").getAsInt() + 1);
        newInstance.addProperty("contextPath", "newPath");
        result = utils.checkIfInstanceAlreadyExist(newInstance);

        assertEquals("Instance should not exist: '" + result + "'.", false, result);
    }

    @Test
    public void testCheckIfInstanceNameAlreadyExist() {
        when(fileUtils.getInstancesFromFile()).thenReturn(instances);
        JsonObject newInstance;
        boolean result;

        // Changing instance host, port and path, but keeps name
        newInstance = instance.getAsJsonObject();
        newInstance.addProperty("host", "newHost");
        newInstance.addProperty("port", newInstance.get("port").getAsInt() + 1);
        newInstance.addProperty("contextPath", "newPath");

        result = utils.checkIfInstanceNameAlreadyExist(newInstance);
        assertEquals("Instance name should already exist: '" + result + "'.", true, result);

        // Changing instance name and it should pass check
        newInstance.addProperty("name", "newName");
        result = utils.checkIfInstanceNameAlreadyExist(newInstance);

        assertEquals("Instance name should not exist: '" + result + "'.", false, result);
    }

    @Test
    public void testGetBackEndInformationByName() {
        // Test when name was found.
        when(fileUtils.getInstancesFromFile()).thenReturn(instances);

        String nameToGet = instances.get(0).getAsJsonObject().get("name").getAsString();
        BackEndInformation result = utils.getBackEndInformationByName(nameToGet);

        assertEquals("Expected instance data:\n" + instances.get(0).getAsJsonObject() + "\nBut got data:\n"
                + result.getAsJsonObject(), instances.get(0).getAsJsonObject(), result.getAsJsonObject());

        // Test where default Back End was returned
        utils.setDefaultBackendInformation(
                new BackEndInformation("otherName", "otherHost", "9998", "otherPath", false, true));
        JsonObject defaultBackEnd = utils.getDefaultBackendInformation().getAsJsonObject();

        BackEndInformation result2 = utils.getBackEndInformationByName(null);

        assertEquals("Expected default data:\n" + defaultBackEnd + "\nBut got data:\n" + result2.getAsJsonObject(),
                defaultBackEnd, result2.getAsJsonObject());

        // Get first present back end
        utils.getDefaultBackendInformation().setHost(null);

        BackEndInformation result3 = utils.getBackEndInformationByName(null);
        assertEquals("Expected first available instance:\n" + instances.get(0).getAsJsonObject() + "\nBut got data:\n"
                + result3.getAsJsonObject(), instances.get(0).getAsJsonObject(), result3.getAsJsonObject());

        // No back end data exist
        when(fileUtils.getInstancesFromFile()).thenReturn(new JsonArray());
        BackEndInformation result4 = utils.getBackEndInformationByName(null);
        assertEquals("Expected 'null' but got:\n" + String.valueOf(result4), null, result4);

    }

    @Test
    public void testAddNewBackEnd() {
        when(fileUtils.getInstancesFromFile()).thenReturn(new JsonArray());
        utils.addNewBackEnd(instance);
        assertEquals(instance, utils.getBackEndInformationList().get(0).getAsJsonObject());
    }

    @Test
    public void testDeleteBackEnd() {
        when(fileUtils.getInstancesFromFile()).thenReturn(instances);
        assertEquals(true, utils.checkIfInstanceAlreadyExist(instance));
        utils.deleteBackEnd(instance);
        List<BackEndInformation> backEndList = utils.getBackEndInformationList();
        for (BackEndInformation backend : backEndList) {
            assertEquals(false, backend.getName().equals(instance.get("name").getAsString()));
        }
    }

    @Test
    public void testGetBackEndsAsJsonArray() {
        when(fileUtils.getInstancesFromFile()).thenReturn(instances);
        JsonArray result = utils.getBackEndsAsJsonArray();
        assertEquals(instances, result);
    }
}
