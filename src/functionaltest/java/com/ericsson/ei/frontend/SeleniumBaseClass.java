package com.ericsson.ei.frontend;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ericsson.ei.config.SeleniumConfig;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SeleniumBaseClass extends TestBaseClass {
    @Autowired
    @InjectMocks
    EIRequestsController eIRequestsController;

    protected FirefoxDriver driver;
    protected String baseUrl;

    private StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        driver = SeleniumConfig.initFirefoxDriver();
        baseUrl = SeleniumConfig.getBaseUrl(testServerPort);
    }

    @After
    public void tearDown() throws Exception {
        File tempDownloadDirectory = SeleniumConfig.getTempDownloadDirectory();
        FileUtils.deleteDirectory(tempDownloadDirectory);

        String verificationErrorString = verificationErrors.toString();
        if (!verificationErrorString.equals("")) {
            fail(verificationErrorString);
        }

        backEndInstancesUtils.setDefaultBackEndInstanceToNull();
    }

    protected void initBaseMocks(CloseableHttpClient mockedHttpClient) throws ClientProtocolException, IOException {
        CloseableHttpResponse responseData = createMockedHTTPResponse("\"\":\"\"", 200);
        Mockito.doReturn(responseData).when(mockedHttpClient)
                .execute(Mockito.argThat(request -> (request).getURI().toString().contains("/auth/check-status")));

        Mockito.doReturn(responseData).when(mockedHttpClient)
                .execute(Mockito.argThat(request -> (request).getURI().toString().contains("/auth")));

        Mockito.doReturn(responseData).when(mockedHttpClient)
                .execute(Mockito.argThat(request -> (request).getURI().toString().contains("/subscriptions")));

        Mockito.doReturn(responseData).when(mockedHttpClient).execute(Mockito
                .argThat(request -> (request).getURI().toString().contains("/rules/rule-check/test-rule-page-enabled")));

    }

    private CloseableHttpResponse createMockedHTTPResponse(String message, int httpStatus) {
        HttpEntity entity = EntityBuilder.create().setText(message).setContentType(ContentType.APPLICATION_JSON)
                .build();
        CloseableHttpResponse mockedHttpResponse = Mockito.mock(CloseableHttpResponse.class);
        mockedHttpResponse.setEntity(entity);
        when(mockedHttpResponse.getStatusLine())
                .thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, httpStatus, "DUMMYRIGHTNOW"));
        when(mockedHttpResponse.getEntity()).thenReturn(entity);
        return mockedHttpResponse;
    }

}
