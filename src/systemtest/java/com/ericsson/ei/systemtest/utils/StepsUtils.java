package com.ericsson.ei.systemtest.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONException;

public class StepsUtils {

    /**
    *
    * This function creates a job in jenkins with an attached script
    *
    * @param jenkinsJobName - Name of the jenkins job
    * @param scriptFileName - FileName of the script which is to be executed when the job is triggered
    * @param jenkinsBaseUrl - Base url to jenkins e.g http://localhost:8070
    * @param jenkinsUserName - Username to the jenkins machine
    * @param jenkinsPassword - Password to the jenkins machine
    * @param jenkinsToken - Token to the jenkins job.
    *
    * @return
    *
    * @throws JSONException
    * @throws URISyntaxException
    * @throws IOException
    */
    public static void a_jenkins_job_from_is_created(String jenkinsJobName, String scriptFileName, String jenkinsBaseUrl, String jenkinsUsername, String jenkinsPassword, String jenkinsToken) throws URISyntaxException, JSONException, IOException {
        JenkinsManager jenkinsManager;
        jenkinsManager = new JenkinsManager(jenkinsBaseUrl, jenkinsUsername, jenkinsPassword);
        String script = new String(Files.readAllBytes(Paths.get(scriptFileName)));
        String xmlJobData = jenkinsManager.getXmlJobData(jenkinsToken, script);
        jenkinsManager.createJob(jenkinsJobName, xmlJobData);
    }
}
