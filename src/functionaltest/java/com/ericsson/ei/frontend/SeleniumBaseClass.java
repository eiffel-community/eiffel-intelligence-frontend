package com.ericsson.ei.frontend;

import static org.junit.Assert.fail;

import com.ericsson.ei.config.SeleniumConfig;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
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

    protected CloseableHttpResponse mockedHttpResponse;

    @LocalServerPort
    private int randomServerPort;

    @Autowired
    @InjectMocks
    EIRequestsController eIRequestsController;

    @Autowired
    WebController webController;

    protected WebDriver driver;
    protected String baseUrl;

    private StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        webController.setFrontendServicePort(randomServerPort);
        driver = SeleniumConfig.getFirefoxDriver();
        baseUrl = SeleniumConfig.getBaseUrl(randomServerPort);
        mockedHttpResponse = Mockito.mock(CloseableHttpResponse.class);
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }
}
