package com.ericsson.ei.frontend.pageobjects;


import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.mockito.Mockito.when;

import com.ericsson.ei.frontend.EIRequestsController;

public class PageBaseClass {
    EIRequestsController mockEIRequestsController;
    CloseableHttpClient mockedHttpClient;
    CloseableHttpResponse mockedHttpResponse;

    protected WebDriver driver;
    protected String baseUrl;

    public PageBaseClass(CloseableHttpClient mockedHttpClient, CloseableHttpResponse mockedHttpResponse, WebDriver driver,
            String baseUrl) {
        super();
        this.mockedHttpClient = mockedHttpClient;
        this.mockedHttpResponse = mockedHttpResponse;
        this.driver = driver;
        this.baseUrl = baseUrl;
        PageFactory.initElements(driver, this);
    }

    public void waitForJQueryToLoad() {
        WebDriverWait webDriverWait = new WebDriverWait(driver, 30);
        webDriverWait.until((ExpectedCondition<Boolean>) wd ->
                ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));

        webDriverWait.until((ExpectedCondition<Boolean>) wd ->
        ((JavascriptExecutor) wd).executeScript("return jQuery.active==0").equals(true));
    }

    protected CloseableHttpResponse createMockedHTTPResponse(String message, int httpStatus) {
    	HttpEntity entity = EntityBuilder.create()
    			.setText(message)
    			.setContentType(ContentType.APPLICATION_JSON)
    			.build();

    	mockedHttpResponse.setEntity(entity);

    	when(mockedHttpResponse.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, httpStatus, "DUMMYRIGHTNOW"));
		when(mockedHttpResponse.getEntity()).thenReturn(entity);

    	return mockedHttpResponse;
    }
}
