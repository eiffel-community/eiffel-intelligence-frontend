package com.ericsson.ei.systemtest.sourcechangeflow;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/systemtest/resources/features/SourceChangeFlow.feature", glue = {
        "com.ericsson.ei.systemtest.sourcechangeflow" }, plugin = { "pretty",
        "html:target/cucumber-reports/SourceChangeFlow"})
public class SourceChangeFlowRunner {
}