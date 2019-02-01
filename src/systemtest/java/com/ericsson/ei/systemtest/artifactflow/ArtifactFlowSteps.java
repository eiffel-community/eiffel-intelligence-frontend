package com.ericsson.ei.systemtest.artifactflow;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.json.JSONException;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.xml.sax.SAXException;

import com.ericsson.ei.systemtest.utils.Config;
import com.ericsson.ei.systemtest.utils.JenkinsManager;
import com.ericsson.ei.systemtest.utils.StepsUtils;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@Ignore
public class ArtifactFlowSteps extends AbstractTestExecutionListener{
    private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactFlowSteps.class);
    private static final String JENKINS_TOKEN = "123";

    Config config = new Config();
    private JenkinsManager jenkinsManager;

    DocumentBuilderFactory xmlDocumentFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder xmlDocumentBuilder;

    @Given("^configurations are provided$")
    public void configurations_are_provided() {
        //Temporary for my change(done in another PR)
        config.initJenkinsConfig();
    }

    @Given("^some subscriptions are set up in another story etc$")
    public void some_subscriptions_are_set_up_in_another_story_etc() {
        // Write code here that turns the phrase above into concrete actions
    }

    @Given("^a jenkins job '\\\"([^\\\"]*)\\\"' from '\"([^\"]*)\"' is created$")
    public void a_jenkins_job_from_is_created(String jenkinsJobName, String scriptFileName) throws URISyntaxException, JSONException, IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        StepsUtils.a_jenkins_job_from_is_created(jenkinsJobName, scriptFileName, config.getJenkinsBaseUrl(), config.getJenkinsUsername(), config.getJenkinsPassword(), JENKINS_TOKEN);
    }

    @When("^next story happens$")
    public void next_story_happens() {
        // Write code here that turns the phrase above into concrete actions
    }

    @Then("^all is good$")
    public void all_is_good() {
        // Write code here that turns the phrase above into concrete actions
    }
}
