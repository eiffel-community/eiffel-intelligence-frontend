package com.ericsson.ei.systemtest.flowRunner;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/systemtest/resources/features/test.feature", glue = {
        "com.ericsson.ei.systemtests" }, plugin = { "pretty", "html:target/cucumber-reports/FlowRunner" })
public class FlowRunnerIT {

    @BeforeClass
    public static void init() throws Throwable {

    }
}
