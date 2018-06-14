package com.ericsson.ei.frontend.pageobjects;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.ericsson.ei.frontend.EIRequestsController;

public class SubscriptionPage extends PageBaseClass {

    public SubscriptionPage(CloseableHttpClient mockedHttpClient, WebDriver driver, String baseUrl) throws ClientProtocolException, IOException {
        super(mockedHttpClient, driver, baseUrl);
    }

    public String getMainHeader() {
        WebElement mainHeader = driver.findElement(By.xpath("//div[@class='container pull-left']//h1"));
        return mainHeader.getText();
    }

    public void clickAddSubscription() {
        // waitForJQueryToLoad();
        WebElement addSubscriptionBtn = driver
                .findElement(By.xpath("//button[contains(@title,'Add a new subscription to EI')]"));
        addSubscriptionBtn.click();
    }
    
    public void clickBulkDelete() {
        // waitForJQueryToLoad();
        WebElement addSubscriptionBtn = driver
                .findElement(By.xpath("//button[contains(@title,'Delete all marked subscriptions from EI')]"));
        addSubscriptionBtn.click();
    }
    
    public void clickReload() {
        // waitForJQueryToLoad();
        WebElement addSubscriptionBtn = driver
                .findElement(By.xpath("//button[contains(@title,'Reload all subscriptions data from EI')]"));
        addSubscriptionBtn.click();
    }
    
    public void clickGetTemplate() {
        // waitForJQueryToLoad();
        WebElement addSubscriptionBtn = driver
                .findElement(By.xpath("//button[contains(@title,'Download Subscription JSON template')]"));
        addSubscriptionBtn.click();
    }
    
    public void clickUploadSubscription() throws AWTException {
        // waitForJQueryToLoad();
        WebElement uploadSubscriptionBtn = driver
                .findElement(By.xpath("//button[contains(@title,'Upload Subscription JSON')]"));
        uploadSubscriptionBtn.click();        

        System.setProperty("java.awt.headless", "false");
        
        StringSelection ss = new StringSelection("C:\\Users\\ezsahoi\\Desktop\\subUpload.txt");
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss,null);
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

    public void clickFormsSaveBtn() {
        WebElement saveBtn = driver.findElement(By.xpath("//button[contains(@title,'Save the changes to EI.')]"));
        saveBtn.click();
    }
    
    public void clickFormsCancelBtn() {
        WebElement saveBtn = driver.findElement(By.xpath("//button[contains(@title,'Cancel and abort all changes')]"));
        saveBtn.click();
    }

    public void selectTemplate(String ss) {
        WebElement selectTemplateBtn = driver
                .findElement(By.xpath("//select[contains(@title,'Choose a Subscription Template')]"));
        Select dropdown = new Select(selectTemplateBtn);
        // dropdown.getFirstSelectedOption().getText();
        dropdown.selectByVisibleText(ss);
    }

    public String getValueFromSelect() {
        WebElement selectNotificationType = driver
                .findElement(By.xpath("//select[contains(@title,'Choose a notification type')]"));
        Select dropdown = new Select(selectNotificationType);
        return dropdown.getFirstSelectedOption().getText();
    }
    
    public String getValueFromElement() {
        return driver.findElement(By.xpath("//textarea[contains(@title,'Specify notification meta data')]")).getAttribute("value");        
    }
    
    public void addSubscriptionName(String subscriptionName) {
        WebElement txt = driver.findElement(By.xpath("//input[contains(@title,'Specify a SubsciptionName')]"));
        txt.clear();
        txt.sendKeys(subscriptionName);   

    }
    

}
