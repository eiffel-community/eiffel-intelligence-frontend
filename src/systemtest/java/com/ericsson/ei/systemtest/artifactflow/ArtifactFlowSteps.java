package com.ericsson.ei.systemtest.artifactflow;

import java.util.ArrayList;

import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import com.ericsson.ei.systemtest.utils.Config;
import com.ericsson.ei.systemtest.utils.StepsUtils;

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
        //Temporary for my change(done in another PR)
        config.initJenkinsConfig();
        config.initRemRemConfig();
    }

    @Given("^some subscriptions are set up$")
    public void some_subscriptions_are_set_up_in_another_story_etc() {
        // Write code here that turns the phrase above into concrete actions
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
    }

    @When("^next story happens$")
    public void next_story_happens() {
        // Write code here that turns the phrase above into concrete actions
    }

    @Then("^all is good$")
    public void all_is_good() {
        // Write code here that turns the phrase above into concrete actions
    }

    @Then("^subscriptions and jenkins jobs should be removed$")
    public void subscriptions_and_jenkins_jobs_should_be_removed() throws Throwable {
        StepsUtils.deleteJenkinsJobs(jenkinsJobNames);
    }
}
