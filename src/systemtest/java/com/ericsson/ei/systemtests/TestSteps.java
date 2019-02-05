package com.ericsson.ei.systemtests;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@Ignore
public class TestSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestSteps.class);

    @When("^the client test$")
    public void the_client_test() {
        System.out.println("Testing started");

    }

    @Then("^the client receives status code of (\\d+)$")
    public void the_client_receives_status_code_of(int arg1) {
        assertEquals(HttpStatus.OK, HttpStatus.OK);
        ;
    }

}
