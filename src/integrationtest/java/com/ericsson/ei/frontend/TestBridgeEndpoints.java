package com.ericsson.ei.frontend;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.ericsson.ei.config.Utils;

public class TestBridgeEndpoints {

    private static String PROPERTIES_PATH = "/integration-test.properties";

    private static String backendPort;

    @Before
    public void beforeTest() {
        String filePath = this.getClass().getResource(PROPERTIES_PATH).getFile();
        final Properties properties = Utils.getProperties(filePath);
        backendPort = properties.getProperty("ei.it.backend-port");
    }
    @Test
    public void test() {
        System.out.println("This is the backend port: " + backendPort);
    }

}