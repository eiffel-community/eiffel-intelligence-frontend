package com.ericsson.ei.systemtest.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.eiffelcommons.JenkinsManager;
import com.ericsson.eiffelcommons.subscriptionobject.RestPostSubscriptionObject;
import com.ericsson.eiffelcommons.utils.Utils;

public class StepsUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(StepsUtils.class);

    private static JenkinsManager jenkinsManager;
    private static JSONObject subscriptions = new JSONObject();
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
     * @throws Exception
    */
    public static boolean createJenkinsJob(String jenkinsJobName, String scriptFileName, String jenkinsBaseUrl, String jenkinsUsername, String jenkinsPassword, String jenkinsToken, String jenkinsJobXml, String remremBaseUrl) throws Exception {
        jenkinsManager = new JenkinsManager(jenkinsBaseUrl, jenkinsUsername, jenkinsPassword);

        if(!jenkinsManager.pluginExists("Groovy")) {
            jenkinsManager.installPlugin("Groovy", "2.1");
            jenkinsManager.restartJenkins();
        }
        String script = new String(Files.readAllBytes(Paths.get(scriptFileName)));
        script = script.replace("REMREM_BASE_URL_TO_BE_REPLACED", remremBaseUrl);

        String xmlJobData = Utils.getResourceFileAsString(jenkinsJobXml);
        xmlJobData = xmlJobData.replace("SCRIPT_TO_BE_REPLACED", script);
        return jenkinsManager.forceCreateJob(jenkinsJobName, xmlJobData);
    }

    /**
     *
     * This is a function that removes the jenkins jobs after the testing is done.
     * @param jenkinsJobNames - A list with the jobs to remove from jenkins
     *
     * @return
     * @throws Exception
     */
    public static void deleteJenkinsJobs(ArrayList<String> jenkinsJobNames) throws Exception {
        for (int i = 0; i < jenkinsJobNames.size(); i++) {
            String jenkinsJobName = jenkinsJobNames.get(i);
            boolean success = jenkinsManager.deleteJob(jenkinsJobName);

            if (!success) {
                LOGGER.error("Failed to remove job: \"" + jenkinsJobName+ "\" from jenkins");
            }
        }
    }

    public static void createSubscription(String subscriptionName, String nameOfTriggeredJob, String jenkinsUserame, String jenkinsPassword, String jenkinsBaseUrl) throws IOException, JSONException {
        RestPostSubscriptionObject subscription = new RestPostSubscriptionObject(subscriptionName);
        subscription.setRestPostBodyMediaType("application/x-www-form-urlencoded");
        subscription.setBasicAuth(jenkinsUserame, jenkinsPassword);
        subscription.setNotificationMeta(jenkinsBaseUrl + "/job/" + nameOfTriggeredJob + "/buildWithParameters");
        subscriptions.put(subscriptionName, subscription);
    }

    public static void addNotificationToSubscription(String key, String value, String subscriptionName) throws JSONException {
        RestPostSubscriptionObject subscription = (RestPostSubscriptionObject) subscriptions.get(subscriptionName);
        subscription.addNotificationMessageKeyValue(key, value);
    }
}
