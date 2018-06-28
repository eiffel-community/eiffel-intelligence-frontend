package com.ericsson.ei.frontend;

import static org.junit.Assert.fail;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.ericsson.ei.config.SeleniumConfig;

import org.apache.tomcat.util.http.fileupload.FileUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

import org.mockito.MockitoAnnotations;
import org.openqa.selenium.firefox.FirefoxDriver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SeleniumBaseClass {
    @LocalServerPort
    private int randomServerPort;

    @Autowired
    @InjectMocks
    EIRequestsController eIRequestsController;

    @Autowired
    WebController webController;


    protected FirefoxDriver driver;
    protected String baseUrl;

    private StringBuffer verificationErrors = new StringBuffer();

    private static final String BACKEND_INSTANCES_INFORMATION_FILEPATH = String.join(
            File.separator, "src", "main", "resources", "EIBackendInstancesInformation.json");
    private static final String DEFAULT_INSTANCES_INFORMATION_FILEPATH = String.join(
            File.separator, "src", "functionaltest", "resources", "defaultInstancesInformation.json");

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        webController.setFrontendServicePort(randomServerPort);

        driver = SeleniumConfig.getFirefoxDriver();
        baseUrl = SeleniumConfig.getBaseUrl(randomServerPort);
    }

    @After
    public void tearDown() throws Exception {

        File tempDownloadDirectory = SeleniumConfig.getTempDownloadDirectory();
        FileUtils.deleteDirectory(tempDownloadDirectory);

        // Reset whats inside EIBackendInstancesInformation since test will fail if run multiple times otherwise
        String eiBackendInstanceDefaultInformation = getJSONStringFromFile(DEFAULT_INSTANCES_INFORMATION_FILEPATH);
        FileWriter backendInstancesInformationWriter = new FileWriter(BACKEND_INSTANCES_INFORMATION_FILEPATH, false);
        backendInstancesInformationWriter.write(eiBackendInstanceDefaultInformation);
        backendInstancesInformationWriter.close();

        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!verificationErrorString.equals("")) {
            fail(verificationErrorString);
        }
    }

    protected String getJSONStringFromFile(String filepath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filepath)), StandardCharsets.UTF_8).replaceAll("[\\r\\n ]", "");
    }

}
