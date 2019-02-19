package com.ericsson.ei.systemtest.artifactflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
public class ArtifactFlowSteps extends AbstractTestExecutionListener{
    private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactFlowSteps.class);
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

    @Given("^a jenkins job '\\\"([^\\\"]*)\\\"' from '\"([^\"]*)\"' is created$")
    public void a_jenkins_job_from_is_created(String jenkinsJobName, String scriptFileName) throws Throwable {
        boolean success = StepsUtils.createJenkinsJob(
                jenkinsJobName,
                scriptFileName,
                config.getJenkinsBaseUrl(),
                config.getJenkinsUsername(),
                config.getJenkinsPassword(),
                JENKINS_TOKEN,
                JENKINS_JOB_XML,
                config.getRemremBaseUrl()
         );

        if (success) {
            jenkinsJobNames.add(jenkinsJobName);
        }

        assertTrue("Failed to create jenkins job.", success);
    }

    @Then("^we continue with the next step$")
    public void we_continue_with_the_next_step() {
        //Just for cucumber to make sense
    }

    @Then("^subscriptions and jenkins jobs should be removed$")
    public void subscriptions_and_jenkins_jobs_should_be_removed() throws Throwable {
        StepsUtils.deleteJenkinsJobs(jenkinsJobNames);
        StepsUtils.deleteSubscriptions(config.getEiFrontendBaseUrl(), config.getEiBackendBaseUrl());
    }

    @Given("^subscription object \"([^\"]*)\" is created which will trigger \"([^\"]*)\"$")
    public void subscription_is_created(String subscriptionName, String nameOfTriggeredJob) throws Throwable {
        StepsUtils.createSubscription(subscriptionName, nameOfTriggeredJob, config.getJenkinsUsername(), config.getJenkinsPassword(), config.getJenkinsBaseUrl());
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

    @Given("^the jenkins job \"([^\"]*)\" is triggered$")
    public void the_jenkins_job_is_triggered(String jenkinsJobToTrigger) throws Throwable {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("EVENT_ID", "");
        parameters.put("TEST_CASE_NAME", "");

        StepsUtils.triggerJenkinsJobWithParameters(jenkinsJobToTrigger, parameters, JENKINS_TOKEN);
    }

    @When("^all jenkins jobs has been triggered$")
    public void the_jenkins_job_has_been_triggered() throws Throwable {
        StepsUtils.hasJenkinsJobsBeenTriggered(jenkinsJobNames);
    }

    @Then("^the test was a succcess$")
    public void the_test_was_a_succcess() {
        //Just for cucumber to make sence
    }
}
