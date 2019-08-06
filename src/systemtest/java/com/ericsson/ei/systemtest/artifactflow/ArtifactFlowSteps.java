package com.ericsson.ei.systemtest.artifactflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import com.ericsson.ei.systemtest.utils.Config;
import com.ericsson.ei.systemtest.utils.PropertiesHandler;
import com.ericsson.ei.systemtest.utils.StepsUtils;
import com.ericsson.eiffelcommons.utils.ResponseEntity;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@Ignore
public class ArtifactFlowSteps extends AbstractTestExecutionListener {
    private static final String JENKINS_TOKEN = "123";

    private ArrayList<String> jenkinsJobNames = new ArrayList<String>();
    private Config config = new Config();

    @Given("^configurations are provided$")
    public void configurations_are_provided() throws Throwable {
        PropertiesHandler.setProperties();
        config.initEIFrontend();
        config.initEIBackend("artifact");
        config.initJenkinsConfig();
        StepsUtils.installGroovy(config.getJenkinsExternalBaseUrl(),
                                 config.getJenkinsUsername(), config.getJenkinsPassword());
        config.initRemRemConfig();
    }

    @Given("^subscription object \"([^\"]*)\" is created which will trigger \"([^\"]*)\"(.*)$")
    public void subscription_is_created(String subscriptionName, String nameOfTriggeredJob, String hasParameters) throws Throwable {
        StepsUtils.createSubscription(subscriptionName, nameOfTriggeredJob, config.getJenkinsUsername(), config.getJenkinsPassword(),
                                      config.getJenkinsInternalBaseUrl(), !hasParameters.isEmpty());
    }

    @Given("^the jenkins job \"([^\"]*)\" is triggered$")
    public void the_jenkins_job_is_triggered(String jenkinsJobToTrigger) throws Throwable {
        StepsUtils.triggerJenkinsJob(jenkinsJobToTrigger, JENKINS_TOKEN);
    }

    @When("^notification with key \"([^\"]*)\" and value \"([^\"]*)\" is added to \"([^\"]*)\"$")
    public void notification_with_key_and_value_is_added_to(String key, String value, String subscriptionName) throws Throwable {
        StepsUtils.addNotificationToSubscription(key, value, subscriptionName);
    }

    @When("^condition with jmespath \"([^\"]*)\" is added to \"([^\"]*)\"$")
    public void condition_with_jmespath_is_added_to(String jmesPath, String subscriptionName) throws Throwable {
        StepsUtils.addConditionToRequirement(jmesPath, subscriptionName);
    }

    @Then("^a jenkins job \"([^\"]*)\" from \"([^\"]*)\" is created with parameters: (.*)$")
    public void a_jenkins_job_from_is_created(String jenkinsJobName, String scriptFileName, List<String> parameters) throws Throwable {
        boolean success = StepsUtils.createJenkinsJob(
                jenkinsJobName,
                scriptFileName,
                config.getJenkinsExternalBaseUrl(),
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

    @Then("^we send the \"([^\"]*)\" to eiffel intelligence for creation\\.$")
    public void we_send_the_to_eiffel_intelligence_for_creation(String subscriptionName) throws Throwable {
        ResponseEntity response = StepsUtils.sendSubscriptionToEiffelIntelligence(subscriptionName, config.getEiFrontendBaseUrl(), config.getEiBackendBaseUrl());

        assertEquals("Failed to create subscription. Response: " + response.getBody(), 200, response.getStatusCode());
    }

    @Then("^all jenkins jobs has been triggered$")
    public void the_jenkins_job_has_been_triggered() throws Throwable {
        StepsUtils.hasJenkinsJobsBeenTriggered(jenkinsJobNames, config.getJobTimeoutMilliseconds());
    }

    @Then("^subscriptions and jenkins jobs should be removed$")
    public void subscriptions_and_jenkins_jobs_should_be_removed() throws Throwable {
        StepsUtils.deleteJenkinsJobs(jenkinsJobNames);
        StepsUtils.deleteSubscriptions(config.getEiFrontendBaseUrl(), config.getEiBackendBaseUrl());
    }
}