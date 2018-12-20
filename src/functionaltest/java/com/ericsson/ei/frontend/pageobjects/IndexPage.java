package com.ericsson.ei.frontend.pageobjects;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;

public class IndexPage extends PageBaseClass {
    public IndexPage(CloseableHttpClient mockedHttpClient, FirefoxDriver driver, String baseUrl) {
        super(mockedHttpClient, driver, baseUrl);
    }

    public boolean presenceOfReloadButton() {
        try {
            driver.findElement(By.className("table_reload"));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public IndexPage loadPage() {
        driver.get(baseUrl);
        waitForJQueryToLoad();
        return this;
    }

    public void loadPageLDAP() throws ClientProtocolException, IOException {
        driver.get(baseUrl);
        waitForJQueryToLoad();
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public TestRulesPage clickTestRulesPage() throws IOException {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("testRulesBtn")));
        WebElement testRulesBtn = driver.findElement(By.id("testRulesBtn"));
        testRulesBtn.click();
        TestRulesPage testRulesPage = new TestRulesPage(mockedHttpClient, driver, baseUrl);
        waitForJQueryToLoad();
        return testRulesPage;
    }

    public SubscriptionPage clickSubscriptionPage() throws IOException {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("subscriptionBtn")));
        WebElement subscriptionBtn = driver.findElement(By.id("subscriptionBtn"));
        subscriptionBtn.click();
        SubscriptionPage subscriptionPage = new SubscriptionPage(mockedHttpClient, driver, baseUrl);
        waitForJQueryToLoad();
        return subscriptionPage;
    }

    public void clickReloadButton() throws IOException {
        WebElement reloadButton = driver.findElement(By.className("table_reload"));
        reloadButton.click();
    }


    public void clickAdminBackendInstancesBtn() {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("adminBackendInstancesBtn")));
        WebElement adminBackendInstancesBtn = driver.findElement(By.id("adminBackendInstancesBtn"));
        adminBackendInstancesBtn.click();
    }

    public AddBackendPage clickAddBackendInstanceBtn() throws IOException {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("addInstanceBtn")));
        WebElement addInstanceBtn = driver.findElement(By.id("addInstanceBtn"));
        addInstanceBtn.click();
        AddBackendPage addBackendPage = new AddBackendPage(mockedHttpClient, driver, baseUrl);
        waitForJQueryToLoad();
        return addBackendPage;
    }

    public SwitchBackendPage clickSwitchBackendInstanceBtn() throws IOException {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("switcherBtn")));
        WebElement addInstanceBtn = driver.findElement(By.id("switcherBtn"));
        addInstanceBtn.click();
        SwitchBackendPage addBackendPage = new SwitchBackendPage(mockedHttpClient, driver, baseUrl);
        waitForJQueryToLoad();
        return addBackendPage;
    }

    public InfoPage clickEiInfoBtn() {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("eiInfoBtn")));
        WebElement eiInfoBtn = driver.findElement(By.id("eiInfoBtn"));
        eiInfoBtn.click();
        InfoPage infoPage = new InfoPage(mockedHttpClient, driver, baseUrl);
        waitForJQueryToLoad();
        return infoPage;
    }

    public void clickSwitchBackendButton() {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("switcherBtn")));
        WebElement switcherBtn = driver.findElement(By.id("switcherBtn"));
        switcherBtn.click();
        waitForJQueryToLoad();
    }

    public void clickAlarmButton(){
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("alertsDropdown")));
        WebElement alarmBtn = driver.findElement(By.id("alertsDropdown"));
        alarmBtn.click();
        waitForJQueryToLoad();
    }
}