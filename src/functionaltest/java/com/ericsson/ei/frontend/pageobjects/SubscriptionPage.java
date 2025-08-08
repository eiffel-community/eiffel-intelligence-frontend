package com.ericsson.ei.frontend.pageobjects;

import java.io.IOException;
import java.time.Duration;

import org.apache.http.impl.client.CloseableHttpClient;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SubscriptionPage extends PageBaseClass {
    private static final String ROUTE = "/#subscriptions";
    private static final String ADD_SUBSCRIPTION_BUTTON_ID = "addSubscription";
    private static final String FORM_CANCEL_BUTTON_ID = "btnFormCancel";
    private static final String CHECK_ALL_BUTTON_ID = "check-all";
    private static final String RELOAD_BUTTON_ID = "reloadButton";
    private static final String SAVE_BUTTON_ID = "btnSave";
    private static final String GET_TEMPLATE_BUTTON_ID = "getTemplateButton";
    private static final String BULK_DOWNLOAD_BUTTON_ID = "bulkDownload";
    private static final String ADD_CONDITION_BUTTON_ID = "addCondition";
    private static final String ADD_REQUIREMENT_BUTTON_ID = "addRequirement";
    private static final String BULK_DELETE_BUTTON_ID = "bulkDelete";
    private static final String CLOSE_BUTTON_CLASS_NAME = "close";

    public SubscriptionPage(CloseableHttpClient mockedHttpClient, FirefoxDriver driver, String baseUrl)
            throws IOException {
        super(mockedHttpClient, driver, baseUrl);
    }

    public SubscriptionPage loadPage() {
        driver.get(baseUrl + ROUTE);
        waitForJQueryToLoad();
        return this;
    }

    public boolean presenceOfHeader(String loc) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.elementToBeClickable(By.id(loc)));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /*
     * Button clicking start.
     */

    public void clickAddSubscription() {
        clickButtonById(ADD_SUBSCRIPTION_BUTTON_ID);
    }

    public void clickFormsCancelBtn() {
        clickButtonById(FORM_CANCEL_BUTTON_ID);
    }

    public void clickCheckAll() {
        clickButtonById(CHECK_ALL_BUTTON_ID);
    }

    public void clickReload() {
        clickButtonById(RELOAD_BUTTON_ID);
    }

    public void clickFormsSaveBtn() {
        clickButtonById(SAVE_BUTTON_ID);
    }

    public void clickGetTemplate() {
        clickButtonById(GET_TEMPLATE_BUTTON_ID);
    }

    public void clickBulkDownload() {
        clickButtonById(BULK_DOWNLOAD_BUTTON_ID);
    }

    public void clickAddConditionBtn() {
        clickButtonById(ADD_CONDITION_BUTTON_ID);
    }

    public void clickAddRequirementBtn() {
        clickButtonById(ADD_REQUIREMENT_BUTTON_ID);
    }

    public void clickReloadLDAP() {
        clickButtonById(RELOAD_BUTTON_ID);
    }

    public void clickBulkDelete() throws IOException {
        clickButtonById(BULK_DELETE_BUTTON_ID);
        // Click confirm button to confirm delete
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(
                ExpectedConditions.elementToBeClickable(By.cssSelector(".confirm-delete .modal-footer .btn-danger")));
        WebElement confirmBtn = driver.findElement(By.cssSelector(".confirm-delete .modal-footer .btn-danger"));
        confirmBtn.click();
    }

    public void clickButtonById(String id) {
        int attempts = 0;
        while (attempts < 2) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
                WebElement button = driver.findElement(By.id(id));
                button.click();
                break;
            } catch (Exception e) {
                attempts++;
            }
        }
    }

    public void clickFormCloseBtn() {
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.elementToBeClickable(By.className(CLOSE_BUTTON_CLASS_NAME)));
        WebElement formCloseButton = driver.findElement(By.className(CLOSE_BUTTON_CLASS_NAME));
        formCloseButton.click();
    }

    /*
     * Button clicking stop.
     */

    public void selectDropdown(String loc, String value) {
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.elementToBeClickable(By.id(loc)));
        WebElement selectEle = driver.findElement(By.id(loc));
        Select dropdown = new Select(selectEle);
        dropdown.selectByVisibleText(value);
    }

    public boolean isRadioCheckboxSelected(String id) {
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.elementToBeClickable(By.id(id)));
        WebElement checkbox = driver.findElement(By.id(id));
        boolean radioBtnIsSelected = checkbox.isSelected();
        return radioBtnIsSelected;
    }

    public boolean isCheckboxSelected(String id) {
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
        WebElement checkbox = driver.findElement(By.id(id));
        boolean isSelected = checkbox.isSelected();
        return isSelected;
    }

    public void clickSpanAroundCheckbox(String id, String spanId) {
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.presenceOfElementLocated(By.id(spanId)));

        WebElement checkbox = driver.findElement(By.id(id));
        WebElement span = driver.findElement(By.id(spanId));

        span.click();

        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.elementSelectionStateToBe(checkbox, true));
    }

    public String getValueFromElement(String id) {
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.elementToBeClickable(By.id(id)));
        WebElement metaTxt = driver.findElement(By.id(id));
        return metaTxt.getAttribute("value");
    }

    public void addFieldValue(String loc, String value) {
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.elementToBeClickable(By.id(loc)));
        WebElement ele = driver.findElement(By.id(loc));
        ele.clear();
        ele.sendKeys(value);
    }

    public void clickUploadSubscriptionFunctionality(String filePath) throws IOException {
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(
                ExpectedConditions.presenceOfElementLocated(By.id("upload_sub")));
        WebElement uploadInputField = driver.findElement(By.id("upload_sub"));
        uploadInputField.sendKeys(filePath);
    }

    public boolean presenceOfClickGetTemplateButton() {
        try {
            driver.findElement(By.id("getTemplateButton"));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public String getSubscriptionNameFromSubscription() {
        new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(
                ExpectedConditions.elementToBeClickable(By.xpath("//tr[@class='odd']/td[3]")));
        WebElement subscriptionNameElement = driver.findElement(By.xpath("//tr[@class='odd']/td[3]"));
        return subscriptionNameElement.getText();
    }

    public boolean expandButtonExist(String XPath) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.elementToBeClickable(By.xpath(XPath)));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean buttonExistByXPath(String XPath) {
        // The row indicates weather or not the del / view and edit buttons has moved
        // down to next row.
        String findInRow;
        try {
            findInRow = "[2]";
            new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(
                    ExpectedConditions.elementToBeClickable(By.xpath(XPath + findInRow)));
            return true;
        } catch (Exception e) {
        }
        try {
            findInRow = "[1]";
            new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(
                    ExpectedConditions.elementToBeClickable(By.xpath(XPath + findInRow)));
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public boolean buttonDisabledByXPath(String XPath) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath(XPath)));
            WebElement element = driver.findElement(By.xpath(XPath));
            return !element.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean textExistsInTable(String txt) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//tr[td[contains(.,'" + txt + "')]]")));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean textDoesNotExistsInTable(String txt) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(
                    ExpectedConditions.invisibilityOfElementLocated(By.xpath("//tr[td[contains(.,'" + txt + "')]]")));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean clickExpandButtonByXPath(String loc) {
        try {
            if (expandButtonExist(loc)) {
                new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.elementToBeClickable(By.xpath(loc)));
                driver.findElement(By.xpath(loc))
                      .click();
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public int countElements(String id) {
        return driver.findElements(By.id(id))
                     .size();
    }

    public void clickButtonByXPath(String XPath) {
        // The row indicates weather or not the del / view and edit buttons has moved
        // down to
        // next row.
        try {
            String Xpath2 = XPath + "[2]";
            new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.elementToBeClickable(By.xpath(Xpath2)));
            driver.findElement(By.xpath(Xpath2))
                  .click();
            return;
        } catch (Exception e) {
        }
        try {
            String Xpath1 = XPath + "[1]";
            new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.elementToBeClickable(By.xpath(Xpath1)));
            driver.findElement(By.xpath(Xpath1))
                  .click();
            return;
        } catch (Exception e) {
        }
    }

    public boolean noPresenceOfHeader(String loc) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_TIMER)).until(ExpectedConditions.invisibilityOfElementLocated(By.id(loc)));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
