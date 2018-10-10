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

    public SwitchBackendPage(CloseableHttpClient mockedHttpClient, FirefoxDriver driver, String baseUrl)
            throws ClientProtocolException, IOException {
        super(mockedHttpClient, driver, baseUrl);
    }

    public Object getInstanceNameAtPosition(int position) {
        new WebDriverWait(driver, TIMEOUT_TIMER)
                .until(ExpectedConditions.presenceOfElementLocated(By.id("BackendInstance" + position + "Name")));
        WebElement backendInstance1Name = driver.findElement(By.id("BackendInstance" + position + "Name"));
        return backendInstance1Name.getText();
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

    public void removeInstanceNumber(int number) {
        new WebDriverWait(driver, TIMEOUT_TIMER)
                .until(ExpectedConditions.elementToBeClickable(By.id("removeBtn" + number)));
        WebElement removeBtn = driver.findElement(By.id("removeBtn1"));
        removeBtn.click();
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
