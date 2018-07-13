package com.ericsson.ei.frontend.pageobjects;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.IOException;

public class TestRulesPage extends PageBaseClass {

    public TestRulesPage(CloseableHttpClient mockedHttpClient, FirefoxDriver driver, String baseUrl) throws IOException {
        super(mockedHttpClient, driver, baseUrl);
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

    public boolean presenceOfClickDownloadRulesTemplateButton() {
        try {
            driver.findElement(By.className("download_rules_template"));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void clickDownloadRulesTemplate(String responseData) throws IOException {
        CloseableHttpResponse response = this.createMockedHTTPResponse(responseData, 200);
        Mockito.doReturn(response).when(mockedHttpClient).execute(Mockito.argThat(request ->
                (request).getURI().toString().contains("/rulesTemplate")));

        WebElement downloadRulesTemplateButton = driver.findElement(By.className("download_rules_template"));
        downloadRulesTemplateButton.click();
    }

    public void clickDownloadEventsTemplate(String responseData) throws IOException {
        CloseableHttpResponse response = this.createMockedHTTPResponse(responseData, 200);
        Mockito.doReturn(response).when(mockedHttpClient).execute(Mockito.argThat(request ->
                (request).getURI().toString().contains("/eventsTemplate")));

        WebElement downloadEventsTemplateButton = driver.findElement(By.className("download_events_template"));
        downloadEventsTemplateButton.click();
    }

    public void uploadRulesTemplate(String filePath) {
        WebElement uploadRulesInputField = driver.findElement(By.id("uploadRulesFile"));
        uploadRulesInputField.sendKeys(filePath);
    }

    public void uploadEventsTemplate(String filePath) {
        WebElement uploadEventsInputField = driver.findElement(By.id("uploadEventsFile"));
        uploadEventsInputField.sendKeys(filePath);
    }

    public String getFirstRuleText() {
        WebElement textArea = driver.findElementsByClassName("formRules").get(0);
        return textArea.getText().replaceAll("[\\n ]", "");
    }

    public String getFirstEventText() {
        WebElement textArea = driver.findElementsByClassName("formEvents").get(0);
        return textArea.getText().replaceAll("[\\n ]", "");
    }

    public void clickDownloadRulesButton() {
        WebElement downloadRulesButton = driver.findElement(By.className("download_rules"));
        downloadRulesButton.click();
    }

    public void clickAddRuleButton() {
        WebElement addRuleButton = driver.findElement(By.className("add_rule"));
        addRuleButton.click();
    }

    public void clickRemoveRuleNumber(int number) {
        WebElement removeRuleButton = driver.findElement(By.id("Rule" + number)).findElement(By.className("fa-trash"));
        removeRuleButton.click();
    }

    public void clickAddEventButton() {
        WebElement addEventButton = driver.findElement(By.className("add_event"));
        addEventButton.click();
    }

    public void clickRemoveEventNumber(int number) {
        WebElement removeEventButton = driver.findElement(By.id("Events" + number)).findElement(By.className("fa-trash"));

        // We need the following two lines in order to be sure that the remove event button is not obscured...
        JavascriptExecutor jse2 = driver;
        jse2.executeScript("arguments[0].scrollIntoView()", removeEventButton);

        removeEventButton.click();
    }

    public void clickFindAggregatedObject(String findAggregatedObjectResponse) throws IOException {
        CloseableHttpResponse response = this.createMockedHTTPResponse(findAggregatedObjectResponse, 200);
        Mockito.doReturn(response).when(mockedHttpClient).execute(Mockito.argThat(request ->
                (request).getURI().toString().contains("/aggregation")));

        WebElement findAggregatedObjectButton = driver.findElement(By.className("find_aggregated_object"));
        findAggregatedObjectButton.click();
    }

    public String getAggregatedResultData() {
        WebElement aggregatedResultDataElement = driver.findElement(By.id("aggregatedresultData"));
        return aggregatedResultDataElement.getAttribute("textContent").replaceAll("[\\n ]", "");
    }
}

