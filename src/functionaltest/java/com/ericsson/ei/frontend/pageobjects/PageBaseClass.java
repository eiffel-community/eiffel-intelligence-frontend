package com.ericsson.ei.frontend.pageobjects;


import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;

import org.mockito.Mockito;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.firefox.FirefoxDriver;

import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PageBaseClass {

    CloseableHttpClient mockedHttpClient;
    CloseableHttpResponse mockedHttpResponse;

    protected FirefoxDriver driver;
    protected String baseUrl;

    public PageBaseClass(CloseableHttpClient mockedHttpClient,
            FirefoxDriver driver, String baseUrl) throws ClientProtocolException, IOException {

        super();
        this.mockedHttpClient = mockedHttpClient;
        this.driver = driver;
        this.baseUrl = baseUrl;
        PageFactory.initElements(driver, this);

        //Dummy response for all requests that happens before the actuall ones we want to test
        CloseableHttpResponse response = this.createMockedHTTPResponse("{\"response\": dummy}", 200);
        Mockito.doReturn(response).when(mockedHttpClient).execute(Mockito.argThat(request -> ((HttpRequestBase)request).getURI().toString().contains("checkStatus")));

    }

    public void waitForJQueryToLoad() {
        WebDriverWait webDriverWait = new WebDriverWait(driver, 30);
        webDriverWait.until((ExpectedCondition<Boolean>) wd -> ((JavascriptExecutor) wd)
                .executeScript("return document.readyState").equals("complete"));
    }

    protected CloseableHttpResponse createMockedHTTPResponse(String message, int httpStatus) {
        HttpEntity entity = EntityBuilder.create().setText(message).setContentType(ContentType.APPLICATION_JSON)
                .build();
        CloseableHttpResponse mockedHttpResponse = Mockito.mock(CloseableHttpResponse.class);

        mockedHttpResponse.setEntity(entity);

        when(mockedHttpResponse.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, httpStatus, "DUMMYRIGHTNOW"));
        when(mockedHttpResponse.getEntity()).thenReturn(entity);

        return mockedHttpResponse;
    }
}
