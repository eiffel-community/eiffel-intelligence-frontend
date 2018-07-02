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
import org.openqa.selenium.support.ui.Select;

public class SubscriptionPage extends PageBaseClass {

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

        WebElement checkbox = driver.findElement(By.id("check-all"));
        checkbox.click();
        WebElement bulkDeleteBtn = driver
                .findElement(By.xpath("//button[contains(@title,'Delete all marked subscriptions from EI')]"));
        bulkDeleteBtn.click();
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.xpath("//button[contains(text(),'confirm')]")).click();

    }

    public void clickReload(String response) throws ClientProtocolException, IOException, InterruptedException {
        CloseableHttpResponse responseData = this.createMockedHTTPResponse(response, 200);
        // Checks that argument in the request contains "subscriptions" endpoint

        Mockito.doReturn(responseData).when(mockedHttpClient).execute(
                Mockito.argThat(request -> ((HttpRequestBase) request).getURI().toString().contains("subscriptions")));
        WebElement reloadBtn = driver
                .findElement(By.xpath("//button[contains(@title,'Reload all subscriptions data from EI')]"));
        reloadBtn.click();
    }

    public void clickGetTemplate(String response) throws ClientProtocolException, IOException, InterruptedException {
        CloseableHttpResponse responseData = this.createMockedHTTPResponse(response, 200);
        Mockito.doReturn(responseData).when(mockedHttpClient).execute(
                Mockito.argThat(request -> ((HttpRequestBase) request).getURI().toString().contains("subscriptions")));

        WebElement getTemplateBtn = driver
                .findElement(By.xpath("//button[contains(@title,'Download Subscription JSON template')]"));
        getTemplateBtn.click();

    }

    public void clickFormsSaveBtn(String response) throws ClientProtocolException, IOException, InterruptedException {
        CloseableHttpResponse responseData = this.createMockedHTTPResponse(response, 200);

        Mockito.doReturn(responseData).when(mockedHttpClient).execute(
                Mockito.argThat(request -> ((HttpRequestBase) request).getURI().toString().contains("subscriptions")));
        WebElement saveBtn = driver.findElement(By.xpath("//button[contains(@title,'Save the changes to EI.')]"));
        saveBtn.click();
    }

    public void clickKVbtn(String path) {
        WebElement kvBtn = driver.findElement(By.xpath(path));
        kvBtn.click();
    }

    public void selectDropdown(String path, String value) {
        WebElement selectEle = driver.findElement(By.xpath(path));
        Select dropdown = new Select(selectEle);
        // dropdown.getFirstSelectedOption().getText();
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

    public void addFieldValue(String path, String value) {
        WebElement ele = driver.findElement(By.xpath(path));
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
