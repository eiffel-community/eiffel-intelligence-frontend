package com.ericsson.ei.frontend;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

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

import com.ericsson.ei.config.SeleniumConfig;
import com.ericsson.ei.frontend.utils.BackEndInstanceFileUtils;
import com.ericsson.ei.frontend.utils.BackEndInstancesUtils;

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

    private String filePath = "";

    @Autowired
    private BackEndInstanceFileUtils backEndInstanceFileUtils;

    @Autowired
    private BackEndInstancesUtils backEndInstancesUtils;

    @Before
    public void setUp() throws Exception {
        File tempFile = File.createTempFile("tempfile", ".json");
        tempFile.deleteOnExit();

        filePath = tempFile.getAbsolutePath().toString();
        Files.write(Paths.get(filePath), "[]".getBytes());
        backEndInstanceFileUtils.setEiInstancesPath(filePath);

        setDefaultBackEndInstanceToNull();
        setDefaultBackEndInstance("test", "localhost", 12345, "");

        MockitoAnnotations.initMocks(this);
        webController.setFrontendServicePort(randomServerPort);

        driver = SeleniumConfig.initFirefoxDriver();
        baseUrl = SeleniumConfig.getBaseUrl(randomServerPort);
    }

    @After
    public void tearDown() throws Exception {
        File tempDownloadDirectory = SeleniumConfig.getTempDownloadDirectory();
        FileUtils.deleteDirectory(tempDownloadDirectory);

        String verificationErrorString = verificationErrors.toString();
        if (!verificationErrorString.equals("")) {
            fail(verificationErrorString);
        }

        setDefaultBackEndInstanceToNull();
    }

    protected void setDefaultBackEndInstanceToNull() throws IOException {
        backEndInstancesUtils.getDefaultBackendInformation().setName(null);
        backEndInstancesUtils.getDefaultBackendInformation().setHost(null);
        backEndInstancesUtils.getDefaultBackendInformation().setPort(null);
        backEndInstancesUtils.getDefaultBackendInformation().setPath(null);
        backEndInstancesUtils.getDefaultBackendInformation().setUseSecureHttpBackend(false);
        backEndInstancesUtils.getDefaultBackendInformation().setDefaultBackend(false);
    }

    protected void setDefaultBackEndInstance(String name, String host, int port, String path) throws IOException {
        backEndInstancesUtils.getDefaultBackendInformation().setName(name);
        backEndInstancesUtils.getDefaultBackendInformation().setHost(host);
        backEndInstancesUtils.getDefaultBackendInformation().setPort(String.valueOf(port));
        backEndInstancesUtils.getDefaultBackendInformation().setPath(path);
        backEndInstancesUtils.getDefaultBackendInformation().setUseSecureHttpBackend(false);
        backEndInstancesUtils.getDefaultBackendInformation().setDefaultBackend(true);
    }

    protected static String getJSONStringFromFile(String filepath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filepath)), StandardCharsets.UTF_8).replaceAll("[\\r\\n ]", "");
    }

}
