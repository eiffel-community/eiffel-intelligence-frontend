package com.ericsson.ei.frontend;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.ericsson.ei.frontend.model.BackendInstance;
import com.ericsson.ei.frontend.utils.BackEndInstancesHandler;
import com.ericsson.ei.frontend.utils.WebControllerUtils;
import com.google.gson.JsonArray;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TestBaseClass {
    @LocalServerPort
    protected int testServerPort;

    @Autowired
    WebControllerUtils webControllerUtils;

    private String filePath = "";

    @Autowired
    protected BackEndInstancesHandler backendInstancesUtils;

    @Autowired
    protected MockMvc mockMvc;

    @PostConstruct
    public void init() throws Exception {
        webControllerUtils.setFrontendServicePort(testServerPort);
    }

    protected static String getJSONStringFromFile(String filepath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filepath)), StandardCharsets.UTF_8).replaceAll("[\\r\\n ]", "");
    }

    protected void setBackendInstance(String name, String host, int port, String contextPath, boolean isDefaultBackend) {
        BackendInstance backendInstance = new BackendInstance();
        backendInstance.setName(name);
        backendInstance.setHost(host);
        backendInstance.setPort(Integer.toString(port));
        backendInstance.setContextPath(contextPath);
        backendInstance.setDefaultBackend(true);

        JsonArray backendInstances = new JsonArray();
        backendInstances.add(backendInstance.getAsJsonObject());
        backendInstancesUtils.setBackendInstances(backendInstances);
    }

    protected void setBackendInstances(JsonArray backendInstances) {
        backendInstancesUtils.setBackendInstances(backendInstances);
    }
}
