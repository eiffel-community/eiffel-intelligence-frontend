package com.ericsson.ei.frontend;

import com.ericsson.ei.frontend.model.BackEndInformation;
import com.ericsson.ei.frontend.utils.BackEndInstancesUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class BackendInstancesUtilsTest {

    private static final String NAME_VAL = "someName";
    private static final String HOST_VAL = "someHost";
    private static final int PORT_VAL = 9999;
    private static final String PATH_VAL = "somePath";
    private static final boolean HTTPS_VAL = false;
    private static final boolean ACTIVE_VAL = false;
    private static final String BACKEND_INSTANCE_FILE_PATH = "src/test/resources/backendInstances/backendInstance.json";
    private static final String BACKEND_INSTANCES_FILE_PATH = "src/test/resources/backendInstances/backendInstances.json";
    private static final String FILE_TO_WRITE = "src/test/resources/backendInstances/fileToWriteInstances.json";
    private static final String FILE_TO_PARSE = "src/test/resources/backendInstances/fileToParseInstances.json";

    @Autowired
    private BackEndInstancesUtils utils;

    @Autowired
    private BackEndInformation info;

    private JsonObject instance;
    private JsonArray instances;

    @Before
    public void before() throws IOException {
        instance = new JsonParser().parse(new FileReader(BACKEND_INSTANCE_FILE_PATH)).getAsJsonObject();
        instances = new JsonParser().parse(new FileReader(BACKEND_INSTANCES_FILE_PATH)).getAsJsonArray();
        utils.setInstances(instances);
    }

    @Test
    public void testSetBackendProperties() {
        BackEndInformation information = new BackEndInformation(NAME_VAL, HOST_VAL, PORT_VAL, PATH_VAL, HTTPS_VAL, ACTIVE_VAL);
        utils.setBackEndProperties(information);
        assertEquals(NAME_VAL, info.getName());
        assertEquals(HOST_VAL, info.getHost());
        assertEquals(PORT_VAL, info.getPort());
        assertEquals(PATH_VAL, info.getPath());
        assertEquals(HTTPS_VAL, info.isHttps());
        assertEquals(ACTIVE_VAL, info.isActive());
    }

    @Test
    public void testCheckIfInstanceAlreadyExistTrue() {
        boolean result = utils.checkIfInstanceAlreadyExist(instance);
        assertEquals(true, result);
    }

    @Test
    public void testCheckIfInstanceAlreadyExistFalse() {
        JsonObject newInstance = instance.getAsJsonObject();
        newInstance.addProperty("host", "newHost");
        newInstance.addProperty("port", newInstance.get("port").getAsInt() + 1);
        newInstance.addProperty("path", "newPath");
        boolean result = utils.checkIfInstanceAlreadyExist(newInstance);
        assertEquals(false, result);
    }

    @Test
    public void testWriteIntoFile() throws IOException {
        Files.createFile(Paths.get(FILE_TO_WRITE));
        utils.setEiInstancesPath(FILE_TO_WRITE);
        utils.writeIntoFile();
        JsonArray infoFromFile = new JsonParser().parse(new String(Files.readAllBytes(Paths.get(FILE_TO_WRITE)))).getAsJsonArray();
        assertEquals(infoFromFile, utils.getInstances());
        Files.deleteIfExists(Paths.get(FILE_TO_WRITE));
    }

    @Test
    public void testParseBackendInstancesFile() throws IOException {
        utils.setEiInstancesPath(FILE_TO_PARSE);
        utils.parseBackEndInstancesFile();
        JsonArray infoFromFile = new JsonParser().parse(new String(Files.readAllBytes(Paths.get(FILE_TO_PARSE)))).getAsJsonArray();
        assertEquals(infoFromFile, utils.getInstances());
    }

}
