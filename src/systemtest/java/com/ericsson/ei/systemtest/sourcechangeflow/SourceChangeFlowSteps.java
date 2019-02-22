package com.ericsson.ei.systemtest.sourcechangeflow;

import com.ericsson.ei.systemtest.utils.Config;
import com.ericsson.ei.systemtest.utils.StepsUtils;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

@Ignore
public class SourceChangeFlowSteps extends AbstractTestExecutionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceChangeFlowSteps.class);
    private static final String JENKINS_TOKEN = "123";

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
        //StepsUtils.deleteSubscriptions(config.getEiFrontendBaseUrl(), config.getEiBackendBaseUrl());
    }
}
