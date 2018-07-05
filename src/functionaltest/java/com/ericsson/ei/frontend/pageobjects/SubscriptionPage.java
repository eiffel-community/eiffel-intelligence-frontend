package com.ericsson.ei.frontend.pageobjects;

import java.io.IOException;
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
    
    public boolean presenceOfHeader(String loc) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.id(loc)));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void clickAddSubscription() {
        WebElement addSubscriptionBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("addSubscription")));
        addSubscriptionBtn.click();
    }

    public void clickFormsCancelBtn() {
        WebElement cancelBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("cancelButton")));
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
        WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("btnSave")));
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
        WebElement selectNotificationType = wait
                .until(ExpectedConditions.elementToBeClickable(By.id("notificationType")));
        Select dropdown = new Select(selectNotificationType);
        return dropdown.getFirstSelectedOption().getText();
    }

    public String getValueFromElement() {
        WebElement metaTxt = wait.until(ExpectedConditions.elementToBeClickable(By.id("metaData")));
        return metaTxt.getAttribute("value");
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
    
    public void clickViewBtn() {
        WebElement viewBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'View')]")));
        viewBtn.click();
    }
    
    public void clickFormCloseBtn() {
        WebElement viewBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("close")));
        viewBtn.click();
    }

}
