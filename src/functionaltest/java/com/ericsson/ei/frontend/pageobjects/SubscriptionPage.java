package com.ericsson.ei.frontend.pageobjects;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class SubscriptionPage extends PageBaseClass {

    public SubscriptionPage(CloseableHttpClient mockedHttpClient, WebDriver driver, String baseUrl)
            throws ClientProtocolException, IOException {
        super(mockedHttpClient, driver, baseUrl);
    }

    public String getMainHeader(String path) {
        WebElement mainHeader = driver.findElement(By.xpath(path));
        return mainHeader.getText();
    }

    public void clickAddSubscription() {
        // waitForJQueryToLoad();
        WebElement addSubscriptionBtn = driver
                .findElement(By.xpath("//button[contains(@title,'Add a new subscription to EI')]"));
        addSubscriptionBtn.click();
    }

    public void clickFormsCancelBtn() {
        WebElement cancelBtn = driver
                .findElement(By.xpath("//button[contains(@title,'Cancel and abort all changes')]"));
        cancelBtn.click();
    }

    public void clickBulkDelete(String response) throws InterruptedException {
        // waitForJQueryToLoad();

        CloseableHttpResponse responseData = this.createMockedHTTPResponse(response, 200);

        // Checks that argument in the request contains "subscriptions" endpoint
        try {
            Mockito.doReturn(responseData).when(mockedHttpClient).execute(Mockito
                    .argThat(request -> ((HttpRequestBase) request).getURI().toString().contains("subscriptions")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        WebElement checkbox = driver.findElement(By.id("check-all"));
        checkbox.click();
        WebElement bulkDeleteBtn = driver
                .findElement(By.xpath("//button[contains(@title,'Delete all marked subscriptions from EI')]"));
        bulkDeleteBtn.click();

        TimeUnit.SECONDS.sleep(6);

        driver.findElement(By.xpath("//button[contains(text(),'confirm')]")).click();

    }

    public void clickReload(String response) {
        // waitForJQueryToLoad();

        CloseableHttpResponse responseData = this.createMockedHTTPResponse(response, 200);

        // Checks that argument in the request contains "subscriptions" endpoint
        try {
            Mockito.doReturn(responseData).when(mockedHttpClient).execute(Mockito
                    .argThat(request -> ((HttpRequestBase) request).getURI().toString().contains("subscriptions")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        WebElement reloadBtn = driver
                .findElement(By.xpath("//button[contains(@title,'Reload all subscriptions data from EI')]"));
        reloadBtn.click();
    }

    public void clickGetTemplate() {
        // waitForJQueryToLoad();
        WebElement addSubscriptionBtn = driver
                .findElement(By.xpath("//button[contains(@title,'Download Subscription JSON template')]"));
        addSubscriptionBtn.click();
    }

    public void clickUploadSubscriptions(String filePath, String response) throws AWTException {
        // A single subscription to upload
        // String response =
        // "[{\"aggregationtype\":\"eiffel-intelligence\",\"created\":1224037895323,\"notificationMeta\":\"http://eiffel-jenkins1:8080/job/ei-artifact-triggered-job/build\",\"notificationType\":\"REST_POST\",\"restPostBodyMediaType\":\"application/x-www-form-urlencoded\",\"notificationMessageKeyValues\":[{\"formkey\":\"json\",\"formvalue\":\"{parameter:
        // [{ name: 'jsonparams', value : to_string(@)
        // }]}\"}],\"repeat\":false,\"requirements\":[{\"conditions\":[{\"jmespath\":\"gav.groupId=='com.othercompany.library'\"}]}],\"subscriptionName\":\"Subscription1\",\"_id\":{\"$oid\":\"5ad6f907c242af3f1469751d\"}},{\"aggregationtype\":\"eiffel-intelligence\",\"created\":1524037895415,\"notificationMeta\":\"http://eiffel-jenkins1:8080/job/ei-artifact-triggered-job/build\",\"notificationType\":\"REST_POST\",\"restPostBodyMediaType\":\"application/x-www-form-urlencoded\",\"notificationMessageKeyValues\":[{\"formkey\":\"json\",\"formvalue\":\"{parameter:
        // [{ name: 'jsonparams', value : to_string(@)
        // }]}\"}],\"repeat\":false,\"requirements\":[{\"conditions\":[{\"jmespath\":\"gav.groupId=='com.othercompany.library'\"}]}],\"subscriptionName\":\"Subscription2\",\"_id\":{\"$oid\":\"5ad6f907c242af3f1469751e\"}},{\"aggregationtype\":\"eiffel-intelligence\",\"created\":1524223397628,\"notificationMeta\":\"http://<MyHost:port>/api/doit\",\"notificationType\":\"REST_POST\",\"restPostBodyMediaType\":\"application/json\",\"notificationMessageKeyValues\":[{\"formkey\":\"\",\"formvalue\":\"{mydata:
        // [{ fullaggregation : to_string(@)
        // }]}\"}],\"repeat\":false,\"requirements\":[{\"conditions\":[{\"jmespath\":\"submission.sourceChanges[?submitter.group
        // == 'Team Gophers' &&
        // svnIdentifier==null]\"}]}],\"subscriptionName\":\"Subscription_uploded\",\"_id\":{\"$oid\":\"5ad9cda5b715d336247e4980\"}}]";

        CloseableHttpResponse responseData = this.createMockedHTTPResponse(response, 200);

        try {
            Mockito.doReturn(responseData).when(mockedHttpClient).execute(Mockito
                    .argThat(request -> ((HttpRequestBase) request).getURI().toString().contains("subscriptions")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        WebElement uploadSubscriptionBtn = driver
                .findElement(By.xpath("//button[contains(@title,'Upload Subscription JSON')]"));
        uploadSubscriptionBtn.click();
        System.setProperty("java.awt.headless", "false");
        StringSelection ss = new StringSelection(filePath);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

        Robot r = new Robot();
        r.keyPress(KeyEvent.VK_ENTER);
        r.keyRelease(KeyEvent.VK_ENTER);
        r.keyPress(KeyEvent.VK_CONTROL);
        r.keyPress(KeyEvent.VK_V);
        r.keyRelease(KeyEvent.VK_V);
        r.keyRelease(KeyEvent.VK_CONTROL);
        r.keyPress(KeyEvent.VK_ENTER);
        r.keyRelease(KeyEvent.VK_ENTER);
    }

    public void clickFormsSaveBtn(String response) {
        CloseableHttpResponse responseData = this.createMockedHTTPResponse(response, 200);

        // Checks that argument in the request contains "subscriptions" endpoint
        try {
            Mockito.doReturn(responseData).when(mockedHttpClient).execute(Mockito
                    .argThat(request -> ((HttpRequestBase) request).getURI().toString().contains("subscriptions")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        WebElement saveBtn = driver.findElement(By.xpath("//button[contains(@title,'Save the changes to EI.')]"));
        saveBtn.click();
    }
    
    public void clickKVbtn(String path) {
        WebElement kvBtn = driver.findElement(By.xpath(path));
        kvBtn.click();
    }

 
    public void selectDropdown(String path, String value) {
        WebElement selectEle = driver
                .findElement(By.xpath(path));
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

   public void addFieldValue(String path , String value){
        WebElement ele = driver.findElement(By.xpath(path));
        ele.clear();
        ele.sendKeys(value);
        
    }

}
