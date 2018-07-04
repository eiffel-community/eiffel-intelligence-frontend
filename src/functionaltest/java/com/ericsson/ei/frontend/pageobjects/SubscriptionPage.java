package com.ericsson.ei.frontend.pageobjects;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SubscriptionPage extends PageBaseClass {
    WebDriverWait wait = new WebDriverWait(driver, 15);

    public SubscriptionPage(CloseableHttpClient mockedHttpClient, FirefoxDriver driver, String baseUrl)
            throws ClientProtocolException, IOException {
        super(mockedHttpClient, driver, baseUrl);
    }

    public boolean presenceOfHeader(String path) {
        try {
            driver.findElement(By.xpath(path));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public Boolean presenceOfSubscriptionButton() {
        try {
            driver.findElement(By.xpath("//button[contains(@title,'Add a new subscription to EI')]"));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void clickAddSubscription() {
        WebElement addSubscriptionBtn = driver
                .findElement(By.xpath("//button[contains(@title,'Add a new subscription to EI')]"));
        addSubscriptionBtn.click();
    }

    public void clickFormsCancelBtn() {
        WebElement cancelBtn = driver
                .findElement(By.xpath("//button[contains(@title,'Cancel and abort all changes')]"));
        cancelBtn.click();
    }

    public void clickBulkDelete(String response) throws ClientProtocolException, IOException, InterruptedException {
        CloseableHttpResponse responseData = this.createMockedHTTPResponse(response, 200);
        Mockito.doReturn(responseData).when(mockedHttpClient).execute(
                Mockito.argThat(request -> ((HttpRequestBase) request).getURI().toString().contains("subscriptions")));
        WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("check-all")));
        checkbox.click();
        WebElement bulkDeleteBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("bulkDelete")));
        bulkDeleteBtn.click();
        // Click confirm button to confirm delete
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'confirm')]"))).click();

    }

    public void clickReload(String response) throws ClientProtocolException, IOException, InterruptedException {
        CloseableHttpResponse responseData = this.createMockedHTTPResponse(response, 200);
        Mockito.doReturn(responseData).when(mockedHttpClient).execute(
                Mockito.argThat(request -> ((HttpRequestBase) request).getURI().toString().contains("subscriptions")));
        WebElement reloadBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("reloadButton")));
        reloadBtn.click();
    }

    public void clickGetTemplate(String response) throws ClientProtocolException, IOException, InterruptedException {
        CloseableHttpResponse responseData = this.createMockedHTTPResponse(response, 200);
        Mockito.doReturn(responseData).when(mockedHttpClient).execute(
                Mockito.argThat(request -> ((HttpRequestBase) request).getURI().toString().contains("subscriptions")));
        WebElement getTemplateBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("getTemplateButton")));
        getTemplateBtn.click();
    }

    public void clickFormsSaveBtn(String response) throws ClientProtocolException, IOException, InterruptedException {
        CloseableHttpResponse responseData = this.createMockedHTTPResponse(response, 200);

        Mockito.doReturn(responseData).when(mockedHttpClient).execute(
                Mockito.argThat(request -> ((HttpRequestBase) request).getURI().toString().contains("subscriptions")));
        WebElement saveBtn = driver.findElement(By.xpath("//button[contains(@title,'Save the changes to EI.')]"));
        saveBtn.click();
    }

    public void clickKVbtn(String loc) {
        WebElement kvBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id(loc)));
        kvBtn.click();
    }

    public void selectDropdown(String loc, String value) {
        WebElement selectEle = wait.until(ExpectedConditions.elementToBeClickable(By.id(loc)));
        Select dropdown = new Select(selectEle);
        dropdown.selectByVisibleText(value);
    }

    public String getValueFromSelect() {
        WebElement selectNotificationType = driver
                .findElement(By.xpath("//select[contains(@title,'Choose a notification type')]"));
        Select dropdown = new Select(selectNotificationType);
        return dropdown.getFirstSelectedOption().getText();
    }

    public String getValueFromElement() {
        return driver.findElement(By.xpath("//textarea[contains(@title,'Specify notification meta data')]"))
                .getAttribute("value");
    }

    public void addFieldValue(String loc, String value) {
        WebElement ele = wait.until(ExpectedConditions.elementToBeClickable(By.id(loc)));
        ele.clear();
        ele.sendKeys(value);
    }

    public void clickUploadSubscriptionFunctionality(String filePath, String subUploadResponse)
            throws ClientProtocolException, IOException, InterruptedException {
        CloseableHttpResponse responseData = this.createMockedHTTPResponse(subUploadResponse, 200);
        Mockito.doReturn(responseData).when(mockedHttpClient).execute(
                Mockito.argThat(request -> ((HttpRequestBase) request).getURI().toString().contains("subscriptions")));

        WebElement uploadInputField = driver.findElement(By.id("upload_sub"));
        uploadInputField.sendKeys(filePath);
    }

    public Boolean presenceOfClickGetTemplateButton() {
        try {
            driver.findElement(By.className("get_subscription_template"));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void clickDownloadGetTemplate(String responseData)
            throws ClientProtocolException, IOException, InterruptedException {
        CloseableHttpResponse response = this.createMockedHTTPResponse(responseData, 200);
        Mockito.doReturn(response).when(mockedHttpClient).execute(Mockito.argThat(
                request -> ((HttpRequestBase) request).getURI().toString().contains("/subscriptionsTemplate")));
        WebElement getTemplateButton = driver.findElement(By.className("get_subscription_template"));
        getTemplateButton.click();
    }

}
