package com.ericsson.ei.frontend;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/integrationtest/resources/features/download.feature", glue = {
        "com.ericsson.ei.frontend" }, plugin = {
                "html:target/cucumber-reports/TestDownloadRunner" })
public class TestDownloadRunner {

}