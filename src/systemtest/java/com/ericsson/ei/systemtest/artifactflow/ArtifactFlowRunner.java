package com.ericsson.ei.systemtest.artifactflow;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/systemtest/resources/features/ArtifactFlow.feature", glue = {
        "com.ericsson.ei.systemtest.artifactflow" }, plugin = { "pretty",
                "html:target/cucumber-reports/ArtifactFlow"})
public class ArtifactFlowRunner {
}