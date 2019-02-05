package com.ericsson.ei.systemtests;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import util.PropertiesHandler;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/systemtest/resources/features/test.feature", glue = {
        "com.ericsson.ei.systemtests" }, plugin = { "pretty", "html:target/cucumber-reports/FlowRunner" })
public class FlowRunnerIT {

    @BeforeClass
    public static void init() throws Throwable {
        PropertiesHandler.setProperties();
    }
}
