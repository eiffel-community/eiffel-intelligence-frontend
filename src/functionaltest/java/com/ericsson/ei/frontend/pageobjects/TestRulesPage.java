package com.ericsson.ei.frontend.pageobjects;

import java.io.IOException;
import java.time.Duration;

import org.apache.http.impl.client.CloseableHttpClient;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestRulesPage extends PageBaseClass {
    private static final String ROUTE = "/#test-rules";

    public TestRulesPage(CloseableHttpClient mockedHttpClient, FirefoxDriver driver, String baseUrl) throws IOException {
        super(mockedHttpClient, driver, baseUrl);
    }

    public TestRulesPage loadPage() {
        driver.get(baseUrl + ROUTE);
        waitForJQueryToLoad();
        return this;
    }

    public boolean presenceOfTestRulesHeader() {
        try {
            driver.findElement(By.id("test_rules_header"));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean presenceOfRuleNumber(int number) {
        try {
            driver.findElement(By.id("Rule" + number));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean presenceOfEventNumber(int number) {
        try {
            driver.findElement(By.id("Events" + number));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void clickDownloadRulesTemplate() throws IOException {
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(
                ExpectedConditions.elementToBeClickable(By.className("download_rules_template")));
        WebElement downloadRulesTemplateButton = driver.findElement(By.className("download_rules_template"));
        downloadRulesTemplateButton.click();
    }

    public void clickDownloadEventsTemplate() throws IOException {
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(
                ExpectedConditions.elementToBeClickable(By.className("download_events_template")));
        WebElement downloadEventsTemplateButton = driver.findElement(By.className("download_events_template"));
        downloadEventsTemplateButton.click();
    }

    public void uploadRulesTemplate(String filePath) {
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(
                ExpectedConditions.presenceOfElementLocated(By.id("uploadRulesFile")));
        WebElement uploadRulesInputField = driver.findElement(By.id("uploadRulesFile"));
        uploadRulesInputField.sendKeys(filePath);
    }

    public void uploadEventsTemplate(String filePath) {
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(
                ExpectedConditions.presenceOfElementLocated(By.id("uploadEventsFile")));
        WebElement uploadEventsInputField = driver.findElement(By.id("uploadEventsFile"));
        uploadEventsInputField.sendKeys(filePath);
    }

    public String getFirstRuleText() {
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.presenceOfElementLocated(By.id("Rule2")));
        WebElement textArea = driver.findElements(By.className("formRules")).get(0);
        return textArea.getText().replaceAll("[\\n ]", "");
    }

    public String getFirstEventText() {
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.presenceOfElementLocated(By.id("Events2")));
        WebElement textArea = driver.findElements(By.className("formEvents")).get(0);
        return textArea.getText().replaceAll("[\\n ]", "");
    }

    public void clickDownloadRulesButton() {
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.elementToBeClickable(By.className("download_rules")));
        WebElement downloadRulesButton = driver.findElement(By.className("download_rules"));
        downloadRulesButton.click();
    }

    public void clickAddRuleButton() {
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.presenceOfElementLocated(By.className("add_rule")));
        WebElement addRuleButton = driver.findElement(By.className("add_rule"));
        addRuleButton.click();
    }

    public void clickRemoveRuleNumber(int number) {
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.presenceOfElementLocated(By.id("Rule" + number)));
        WebElement removeRuleButton = driver.findElement(By.id("Rule" + number)).findElement(By.className("remove-item"));

        // We need the following two lines in order to be sure that the remove event button is not obscured...
        JavascriptExecutor jse2 = driver;
        jse2.executeScript("arguments[0].scrollIntoView()", removeRuleButton);

        removeRuleButton.click();
    }

    public void clickAddEventButton() {
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.elementToBeClickable(By.className("add_event")));
        WebElement addEventButton = driver.findElement(By.className("add_event"));
        addEventButton.click();
    }

    public void clickRemoveEventNumber(int number) {
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.presenceOfElementLocated(By.id("Events" + number)));
        WebElement removeEventButton = driver.findElement(By.id("Events" + number)).findElement(By.className("remove-item"));

        // We need the following two lines in order to be sure that the remove event button is not obscured...
        JavascriptExecutor jse2 = driver;
        jse2.executeScript("arguments[0].scrollIntoView()", removeEventButton);

        removeEventButton.click();
    }

    public void clickFindAggregatedObject() throws IOException {
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.elementToBeClickable(By.className("find_aggregated_object")));

        WebElement findAggregatedObjectButton = driver.findElement(By.className("find_aggregated_object"));
        findAggregatedObjectButton.click();
    }

    public String getAggregatedResultData() {
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.presenceOfElementLocated(By.id("aggregatedObjectContent")));
        WebElement aggregatedResultDataElement = driver.findElement(By.id("aggregatedObjectContent"));
        return aggregatedResultDataElement.getAttribute("textContent").replaceAll("[\\n ]", "");
    }
}
