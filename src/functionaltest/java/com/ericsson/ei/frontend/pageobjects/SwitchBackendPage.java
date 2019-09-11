package com.ericsson.ei.frontend.pageobjects;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SwitchBackendPage extends PageBaseClass {
    private static final String ROUTE = "/#switch-backend";

    public SwitchBackendPage loadPage() {
        driver.get(baseUrl + ROUTE);
        waitForJQueryToLoad();
        return this;
    }

    public SwitchBackendPage(CloseableHttpClient mockedHttpClient, FirefoxDriver driver, String baseUrl)
            throws ClientProtocolException, IOException {
        super(mockedHttpClient, driver, baseUrl);
    }

    public Object getInstanceNameAtPosition(int position) {
        new WebDriverWait(driver, TIMEOUT_TIMER)
                .until(ExpectedConditions.presenceOfElementLocated(By.id("BackendInstance" + position + "Name")));
        WebElement backendInstanceNameElement = driver.findElement(By.id("BackendInstance" + position + "Name"));
        return backendInstanceNameElement.getText();
    }

    public void switchToBackendInstance(int backendNumber) {
        new WebDriverWait(driver, TIMEOUT_TIMER)
                .until(ExpectedConditions.elementToBeClickable(By.id("SelectBackendInstance" + backendNumber)));
        WebElement selectBox = driver.findElement(By.id("SelectBackendInstance" + backendNumber));
        selectBox.click();

        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("switcher")));
        WebElement switcher = driver.findElement(By.id("switcher"));
        switcher.click();
        waitForJQueryToLoad();
    }

    public boolean presenceOfInstance(int number) {
        try {
            driver.findElement(By.id("BackendInstance" + number + "Name"));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

}
