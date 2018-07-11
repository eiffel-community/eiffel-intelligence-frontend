package com.ericsson.ei.frontend.pageobjects;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class InfoPage extends PageBaseClass {
    public InfoPage(CloseableHttpClient mockedHttpClient, FirefoxDriver driver,
            String baseUrl) throws ClientProtocolException, IOException {
        super(mockedHttpClient, driver, baseUrl);
    }

    public String getConnectedBackend() {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.presenceOfElementLocated(By.id("connectedBackend")));
        WebElement connectedBackendTextBox = driver.findElement(By.id("connectedBackend"));
        String connectedBackend = connectedBackendTextBox.getText();
        return connectedBackend;
    }

}
