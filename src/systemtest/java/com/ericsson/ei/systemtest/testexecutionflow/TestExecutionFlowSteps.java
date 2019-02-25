package com.ericsson.ei.systemtest.testexecutionflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import com.ericsson.ei.systemtest.utils.Config;
import com.ericsson.ei.systemtest.utils.StepsUtils;
import com.ericsson.eiffelcommons.utils.ResponseEntity;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@Ignore
public class TestExecutionFlowSteps extends AbstractTestExecutionListener{
    private static final Logger LOGGER = LoggerFactory.getLogger(TestExecutionFlowSteps.class);
    private static final String JENKINS_TOKEN = "123";
    private static final String JENKINS_JOB_XML = "jenkinsJobTemplate.xml";

    private ArrayList<String> jenkinsJobNames = new ArrayList<String>();
    private Config config = new Config();

    @Given("^configurations are provided$")
    public void configurations_are_provided() {
        config.initEIFrontend();
        config.initEIBackend();
        config.initJenkinsConfig();
        config.initRemRemConfig();
    }

    @Given("^a jenkins job '\\\"([^\\\"]*)\\\"' from '\"([^\"]*)\"' is created with parameters: (.*)$")
    public void a_jenkins_job_from_is_created(String jenkinsJobName, String scriptFileName, List<String> parameters) throws Throwable {
        boolean success = StepsUtils.createJenkinsJob(
                jenkinsJobName,
                scriptFileName,
                config.getJenkinsBaseUrl(),
                config.getJenkinsUsername(),
                config.getJenkinsPassword(),
                config.getRemremBaseUrl(),
                JENKINS_TOKEN,
                parameters
         );

        if (success) {
            jenkinsJobNames.add(jenkinsJobName);
        }

        assertTrue("Failed to create jenkins job.", success);
    }

    @Then("^subscriptions and jenkins jobs should be removed$")
    public void subscriptions_and_jenkins_jobs_should_be_removed() throws Throwable {
        StepsUtils.deleteJenkinsJobs(jenkinsJobNames);
        StepsUtils.deleteSubscriptions(config.getEiFrontendBaseUrl(), config.getEiBackendBaseUrl());
    }

    @Given("^subscription object \"([^\"]*)\" is created which will trigger \"([^\"]*)\"$")
    public void subscription_is_created(String subscriptionName, String jenkinsJobName) throws Throwable {
        StepsUtils.createSubscription(subscriptionName, jenkinsJobName, config.getJenkinsUsername(), config.getJenkinsPassword(), config.getJenkinsBaseUrl());
    }

    @When("^notification with key \"([^\"]*)\" and value \"([^\"]*)\" is added to \"([^\"]*)\"$")
    public void notification_with_key_and_value_is_added_to(String key, String value, String subscriptionName) throws Throwable {
        StepsUtils.addNotificationToSubscription(key, value, subscriptionName);
    }

    @When("^condition with jmespath \"([^\"]*)\" is added to \"([^\"]*)\"$")
    public void condition_with_jmespath_is_added_to(String jmesPath, String subscriptionName) throws Throwable {
        StepsUtils.addConditionToRequirement(jmesPath, subscriptionName);
    }

    @Then("^we send the \"([^\"]*)\" to eiffel intelligence for creation\\.$")
    public void we_send_the_to_eiffel_intelligence_for_creation(String subscriptionName) throws Throwable {
        ResponseEntity response = StepsUtils.sendSubscriptionToEiffelIntelligence(subscriptionName, config.getEiFrontendBaseUrl(), config.getEiBackendBaseUrl());

        assertEquals("Failed to create subscription. Response: " + response.getBody(), 200, response.getStatusCode());
    }
}
