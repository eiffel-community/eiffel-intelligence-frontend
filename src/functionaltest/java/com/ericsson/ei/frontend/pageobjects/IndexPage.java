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
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='#test-rules']")));
        WebElement testRulesBtn = driver.findElement(By.xpath("//a[@href='#test-rules']"));
        testRulesBtn.click();
        TestRulesPage testRulesPage = new TestRulesPage(mockedHttpClient, driver, baseUrl);
        waitForJQueryToLoad();
        return testRulesPage;
    }

    public SubscriptionPage clickSubscriptionPage() throws IOException {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='#subscriptions']")));
        WebElement subscriptionBtn = driver.findElement(By.xpath("//a[@href='#subscriptions']"));
        subscriptionBtn.click();
        SubscriptionPage subscriptionPage = new SubscriptionPage(mockedHttpClient, driver, baseUrl);
        waitForJQueryToLoad();
        return subscriptionPage;
    }

    public void clickReloadButton() throws IOException {
        WebElement reloadButton = driver.findElement(By.className("table_reload"));
        reloadButton.click();
    }

    public SwitchBackendPage clickSwitchBackendInstanceBtn() throws IOException {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='#switch-backend']")));
        WebElement switchBackendBtn = driver.findElement(By.xpath("//a[@href='#switch-backend']"));
        switchBackendBtn.click();
        SwitchBackendPage switchBackendPage = new SwitchBackendPage(mockedHttpClient, driver, baseUrl);
        waitForJQueryToLoad();
        return switchBackendPage;
    }

    public void clickEiInfoBtn() {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='#ei-info']")));
        WebElement eiffelInfoBtn = driver.findElement(By.xpath("//a[@href='#ei-info']"));
        eiffelInfoBtn.click();
    }

    public InfoPage clickInformationBtn() {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='#information']")));
        WebElement eiInfoBtn = driver.findElement(By.xpath("//a[@href='#information']"));
        eiInfoBtn.click();
        InfoPage infoPage = new InfoPage(mockedHttpClient, driver, baseUrl);
        waitForJQueryToLoad();
        return infoPage;
    }

    public InfoPage clickRulesBtn() {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='#rules']")));
        WebElement eiInfoBtn = driver.findElement(By.xpath("//a[@href='#rules']"));
        eiInfoBtn.click();
        InfoPage infoPage = new InfoPage(mockedHttpClient, driver, baseUrl);
        waitForJQueryToLoad();
        return infoPage;
    }
}
