package com.ericsson.ei.frontend;

import java.io.File;
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

import com.ericsson.ei.frontend.utils.BackEndInstanceFileUtils;
import com.ericsson.ei.frontend.utils.BackEndInstancesUtils;
import com.ericsson.ei.frontend.utils.WebControllerUtils;

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
    protected BackEndInstanceFileUtils backEndInstanceFileUtils;

    @Autowired
    protected BackEndInstancesUtils backEndInstancesUtils;

    @Autowired
    protected MockMvc mockMvc;

    @PostConstruct
    public void init() throws Exception {
        File tempFile = File.createTempFile("tempfile", ".json");
        tempFile.deleteOnExit();

        filePath = tempFile.getAbsolutePath().toString();
        Files.write(Paths.get(filePath), "[]".getBytes());
        backEndInstanceFileUtils.setEiInstancesPath(filePath);

        backEndInstancesUtils.setDefaultBackEndInstanceToNull();
        backEndInstancesUtils.setDefaultBackEndInstance("test", "localhost", 12345, "", true);

        webControllerUtils.setFrontendServicePort(testServerPort);
    }

    protected static String getJSONStringFromFile(String filepath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filepath)), StandardCharsets.UTF_8).replaceAll("[\\r\\n ]", "");
    }
}
