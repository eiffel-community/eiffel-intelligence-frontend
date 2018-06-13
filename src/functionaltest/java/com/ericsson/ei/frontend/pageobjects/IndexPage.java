package com.ericsson.ei.frontend.pageobjects;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import java.io.IOException;

public class IndexPage extends PageBaseClass {
    public IndexPage(CloseableHttpClient mockedHttpClient, CloseableHttpResponse mockedHttpResponse, WebDriver driver,
            String baseUrl) {
        super(mockedHttpClient, mockedHttpResponse, driver, baseUrl);
    }

    public IndexPage loadPage() {
        driver.get(baseUrl);
        waitForJQueryToLoad();
        return this;
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public TestRulesPage clickTestRulesPage() throws ClientProtocolException, IOException {
    	WebElement testRulesBtn = driver.findElement(By.id("testRulesBtn"));
        testRulesBtn.click();

        TestRulesPage testRulesPage = new TestRulesPage(mockedHttpClient, mockedHttpResponse, driver, baseUrl);
        return testRulesPage;
    }


    public void clickReloadButton() throws ClientProtocolException, IOException {
        CloseableHttpResponse response = this.createMockedHTTPResponse("[{\"aggregationtype\":\"eiffel-intelligence\",\"created\":1524037895385,\"notificationMeta\":\"http://eiffel-jenkins1:8080/job/ei-artifact-triggered-job/build\",\"notificationType\":\"REST_POST\",\"restPostBodyMediaType\":\"application/x-www-form-urlencoded\",\"notificationMessageKeyValues\":[{\"formkey\":\"json\",\"formvalue\":\"{parameter: [{ name: 'jsonparams', value : to_string(@) }]}\"}],\"repeat\":false,\"requirements\":[{\"conditions\":[{\"jmespath\":\"gav.groupId=='com.othercompany.library'\"}]}],\"subscriptionName\":\"Subscription1\",\"_id\":{\"$oid\":\"5ad6f907c242af3f1469751d\"}},{\"aggregationtype\":\"eiffel-intelligence\",\"created\":1524037895415,\"notificationMeta\":\"http://eiffel-jenkins1:8080/job/ei-artifact-triggered-job/build\",\"notificationType\":\"REST_POST\",\"restPostBodyMediaType\":\"application/x-www-form-urlencoded\",\"notificationMessageKeyValues\":[{\"formkey\":\"json\",\"formvalue\":\"{parameter: [{ name: 'jsonparams', value : to_string(@) }]}\"}],\"repeat\":false,\"requirements\":[{\"conditions\":[{\"jmespath\":\"gav.groupId=='com.othercompany.library'\"}]}],\"subscriptionName\":\"Subscription2\",\"_id\":{\"$oid\":\"5ad6f907c242af3f1469751e\"}},{\"aggregationtype\":\"eiffel-intelligence\",\"created\":1524223397628,\"notificationMeta\":\"http://<MyHost:port>/api/doit\",\"notificationType\":\"REST_POST\",\"restPostBodyMediaType\":\"application/json\",\"notificationMessageKeyValues\":[{\"formkey\":\"\",\"formvalue\":\"{mydata: [{ fullaggregation : to_string(@) }]}\"}],\"repeat\":false,\"requirements\":[{\"conditions\":[{\"jmespath\":\"submission.sourceChanges[?submitter.group == 'Team Gophers' && svnIdentifier==null]\"}]}],\"subscriptionName\":\"Subscription_Template_Rest_Post_Raw_Body_Json_Trigger\",\"_id\":{\"$oid\":\"5ad9cda5b715d336247e4980\"}}]", 200);

        when(this.mockedHttpClient.execute(any())).thenReturn(response);
        WebElement reloadButton = driver.findElement(By.className("table_reload"));
        reloadButton.click();
    }
}
