package com.ericsson.ei.systemtest.testexecutionflow;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/systemtest/resources/features/TestExecutionFlow.feature", glue = {
        "com.ericsson.ei.systemtest.testexecutionflow" }, plugin = { "pretty",
                "html:target/cucumber-reports/TestExecutionFlow"})
public class TestExecutionFlowRunner {
}