package com.ericsson.ei.systemtest.utils;

import static org.junit.Assert.assertNotNull;

import lombok.Getter;

public class Config {

    @Getter
    private String jenkinsBaseUrl;
    @Getter
    private String jenkinsUsername;
    @Getter
    private String jenkinsPassword;

    /**
    *
    * This method retrieves the jenkins properties from the system properties.
    *
    * @return
    */
    public void initJenkinsConfig() {
        jenkinsBaseUrl = System.getProperty("JENKINS_BASE_URL");
        jenkinsUsername = System.getProperty("JENKINS_USERNAME");
        jenkinsPassword = System.getProperty("JENKINS_PASSWORD");

        assertNotNull(jenkinsBaseUrl);
        assertNotNull(jenkinsUsername);
        assertNotNull(jenkinsPassword);
    }
}
