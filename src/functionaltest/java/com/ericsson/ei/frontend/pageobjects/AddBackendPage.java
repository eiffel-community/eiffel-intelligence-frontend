package com.ericsson.ei.frontend.pageobjects;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AddBackendPage extends PageBaseClass {

    public AddBackendPage(CloseableHttpClient mockedHttpClient,
            FirefoxDriver driver, String baseUrl) throws ClientProtocolException, IOException {
        super(mockedHttpClient, driver, baseUrl);
    }

    public SwitchBackendPage addBackendInstance(String name, String host, int port, String contextPath) throws ClientProtocolException, IOException {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.presenceOfElementLocated(By.id("backEndName")));
        WebElement backEndNameInput = driver.findElement(By.id("backEndName"));
        backEndNameInput.sendKeys(name);

        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.presenceOfElementLocated(By.id("backEndHost")));
        WebElement backEndHostInput = driver.findElement(By.id("backEndHost"));
        backEndHostInput.sendKeys(host);

        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.presenceOfElementLocated(By.id("backEndPort")));
        WebElement backEndPortInput = driver.findElement(By.id("backEndPort"));
        backEndPortInput.sendKeys(Integer.toString(port));

        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.presenceOfElementLocated(By.id("backEndPath")));
        WebElement backEndPathInput = driver.findElement(By.id("backEndPath"));
        backEndPathInput.sendKeys(contextPath);

        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("addInstanceBtn2")));
        WebElement addInstanceBtn2 = driver.findElement(By.id("addInstanceBtn2"));
        addInstanceBtn2.click();

        SwitchBackendPage addBackendPage = new SwitchBackendPage(mockedHttpClient, driver, baseUrl);
        waitForJQueryToLoad();
        return addBackendPage;
    }
}
