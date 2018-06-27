package com.ericsson.ei.frontend;

import org.junit.*;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import com.ericsson.ei.frontend.pageobjects.IndexPage;
import com.ericsson.ei.frontend.pageobjects.TestRulesPage;

public class TemplateTestCase extends SeleniumBaseClass{

    @Test
    public void testTemplateTestCase() throws Exception {

        //Open indexpage and verify that it is opened
        IndexPage indexPageObject = new IndexPage(mockedHttpClient, driver, baseUrl);
        indexPageObject.loadPage();
        
        TimeUnit.SECONDS.sleep(5);
        assertEquals("Eiffel Intelligence", indexPageObject.getTitle());

        //The line below contains selenium interaction with mocking
        String response = "[{\"aggregationtype\":\"eiffel-intelligence\",\"created\":1524037895385,\"notificationMeta\":\"http://eiffel-jenkins1:8080/job/ei-artifact-triggered-job/build\",\"notificationType\":\"REST_POST\",\"restPostBodyMediaType\":\"application/x-www-form-urlencoded\",\"notificationMessageKeyValues\":[{\"formkey\":\"json\",\"formvalue\":\"{parameter: [{ name: 'jsonparams', value : to_string(@) }]}\"}],\"repeat\":false,\"requirements\":[{\"conditions\":[{\"jmespath\":\"gav.groupId=='com.othercompany.library'\"}]}],\"subscriptionName\":\"Subscription1\",\"_id\":{\"$oid\":\"5ad6f907c242af3f1469751d\"}},{\"aggregationtype\":\"eiffel-intelligence\",\"created\":1524037895415,\"notificationMeta\":\"http://eiffel-jenkins1:8080/job/ei-artifact-triggered-job/build\",\"notificationType\":\"REST_POST\",\"restPostBodyMediaType\":\"application/x-www-form-urlencoded\",\"notificationMessageKeyValues\":[{\"formkey\":\"json\",\"formvalue\":\"{parameter: [{ name: 'jsonparams', value : to_string(@) }]}\"}],\"repeat\":false,\"requirements\":[{\"conditions\":[{\"jmespath\":\"gav.groupId=='com.othercompany.library'\"}]}],\"subscriptionName\":\"Subscription2\",\"_id\":{\"$oid\":\"5ad6f907c242af3f1469751e\"}},{\"aggregationtype\":\"eiffel-intelligence\",\"created\":1524223397628,\"notificationMeta\":\"http://<MyHost:port>/api/doit\",\"notificationType\":\"REST_POST\",\"restPostBodyMediaType\":\"application/json\",\"notificationMessageKeyValues\":[{\"formkey\":\"\",\"formvalue\":\"{mydata: [{ fullaggregation : to_string(@) }]}\"}],\"repeat\":false,\"requirements\":[{\"conditions\":[{\"jmespath\":\"submission.sourceChanges[?submitter.group == 'Team Gophers' && svnIdentifier==null]\"}]}],\"subscriptionName\":\"Subscription_Template_Rest_Post_Raw_Body_Json_Trigger\",\"_id\":{\"$oid\":\"5ad9cda5b715d336247e4980\"}}]";
        indexPageObject.clickReloadButton(response);
        TimeUnit.SECONDS.sleep(10);

        //Click on test rules page button and verify that it is opened
        TestRulesPage testRulesPage = indexPageObject.clickTestRulesPage();
        TimeUnit.SECONDS.sleep(2);
        assertEquals("Test Rules", testRulesPage.getMainHeader());
    }

}