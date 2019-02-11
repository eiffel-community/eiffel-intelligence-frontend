package com.ericsson.ei.systemtest.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StepsUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(StepsUtils.class);

    private static JenkinsManager jenkinsManager;
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
    * @return boolean - If the creation was a success or not
    *
    * @throws JSONException
    * @throws URISyntaxException
    * @throws IOException
    */
    public static boolean createJenkinsJob(String jenkinsJobName, String scriptFileName, String jenkinsBaseUrl, String jenkinsUsername, String jenkinsPassword, String jenkinsToken, String remremBaseUrl) throws URISyntaxException, JSONException, IOException {
        jenkinsManager = new JenkinsManager(jenkinsBaseUrl, jenkinsUsername, jenkinsPassword);
        String script = new String(Files.readAllBytes(Paths.get(scriptFileName)));
        script = script.replace("<REMREM_BASE_URL>", remremBaseUrl);

        String xmlJobData = jenkinsManager.getXmlJobData(jenkinsToken, script);
        return jenkinsManager.createJob(jenkinsJobName, xmlJobData);
    }

    /**
     *
     * This is a function that removes the jenkins jobs after the testing is done.
     * @param jenkinsJobNames - A list with the jobs to remove from jenkins
     *
     * @return
     * @throws URISyntaxException
     */
    public static void deleteJenkinsJobs(ArrayList<String> jenkinsJobNames) throws URISyntaxException {
        for (int i = 0; i < jenkinsJobNames.size(); i++) {
            String jenkinsJobName = jenkinsJobNames.get(i);
            boolean success = jenkinsManager.deleteJob(jenkinsJobName);

            if (!success) {
                LOGGER.error("Failed to remove job: \"" + jenkinsJobName+ "\" from jenkins");
            }
        }
    }
}
