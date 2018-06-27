package com.ericsson.ei.frontend;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.ericsson.ei.config.SeleniumConfig;

import org.apache.http.impl.client.CloseableHttpClient;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SeleniumBaseClass {

    @MockBean
    protected CloseableHttpClient mockedHttpClient;

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
