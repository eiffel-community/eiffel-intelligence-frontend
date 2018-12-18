package com.ericsson.ei.frontend;

import java.util.Properties;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.ericsson.ei.frontend.EIFrontendApplication;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;

import com.ericsson.ei.config.Utils;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EIFrontendApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = EIFrontendApplication.class, loader = SpringBootContextLoader.class)
public class TestBridgeEndpointsSteps {

    private static String PROPERTIES_PATH = "/integration-test.properties";
    private static String backendPort;

    @Before
    public void beforeTest() {
        String filePath = this.getClass().getResource(PROPERTIES_PATH).getFile();
        final Properties properties = Utils.getProperties(filePath);
        backendPort = properties.getProperty("ei.it.backend-port");
    }

    @Given("^frontend is up and running$")
//    @Test
    public void test() {
        System.out.println("This is the backend port: " + backendPort);
    }
}