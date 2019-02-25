@ArtifactFlowFeature
Feature: Artifact system test

  @ArtifactFlowScenario
  Scenario: Creates jenkins jobs, subscriptions and at last see that the flowcompletejob has been 
            triggered in order to know if all events has been aggregated correctly.

    Given configurations are provided
    Then a jenkins job "ArtC2Job" from "src/systemtest/resources/JenkinsShellScripts/ArtC2Script.txt" is created with parameters: ,
    And a jenkins job "TCTJob" from "src/systemtest/resources/JenkinsShellScripts/TCTScript.txt" is created with parameters: EVENT_ID, TEST_CASE_NAME,
    And a jenkins job "TCSTCFJob" from "src/systemtest/resources/JenkinsShellScripts/TCSTCFScript.txt" is created with parameters: EVENT_ID,
    And a jenkins job "CLMJob" from "src/systemtest/resources/JenkinsShellScripts/CLMScript.txt" is created with parameters: EVENT_ID,
    And a jenkins job "ArtPJob" from "src/systemtest/resources/JenkinsShellScripts/ArtPScript.txt" is created with parameters: EVENT_ID,
    And a jenkins job "FlowCompleteJob" from "src/systemtest/resources/JenkinsShellScripts/FlowCompleteScript.txt" is created with parameters: EVENT_ID,

    #####Add CLMSubscription to EI#####
    Given subscription object "CLMSubscription" is created which will trigger "CLMJob"
    When notification with key "EVENT_ID" and value "id" is added to "CLMSubscription"
    And condition with jmespath "gav.artifactId=='ArtC2'" is added to "CLMSubscription"
    Then we send the "CLMSubscription" to eiffel intelligence for creation.

    #####Add TCTSubscription to EI#####
    Given subscription object "TCTSubscription" is created which will trigger "TCTJob"
    When notification with key "EVENT_ID" and value "id" is added to "TCTSubscription"
    And notification with key "TEST_CASE_NAME" and value "'Test1'" is added to "TCTSubscription"
    And condition with jmespath "gav.artifactId=='ArtC2'" is added to "TCTSubscription"
    Then we send the "TCTSubscription" to eiffel intelligence for creation.

    #####Add TCSTCFSubscription to EI#####
    Given subscription object "TCSTCFSubscription" is created which will trigger "TCSTCFJob"
    When notification with key "EVENT_ID" and value "testCaseExecutions[0].testCaseTriggeredEventId" is added to "TCSTCFSubscription"
    And condition with jmespath "testCaseExecutions[0].outcome.id=='Test1'" is added to "TCSTCFSubscription"
    Then we send the "TCSTCFSubscription" to eiffel intelligence for creation.

    #####Add ArtPSubscription to EI#####
    Given subscription object "ArtPSubscription" is created which will trigger "ArtPJob"
    When notification with key "EVENT_ID" and value "id" is added to "ArtPSubscription"
    And condition with jmespath "gav.artifactId=='ArtC2'" is added to "ArtPSubscription"
    Then we send the "ArtPSubscription" to eiffel intelligence for creation.

    #####Add FlowCompleteSubscription#####
    Given subscription object "FlowCompleteSubscription" is created which will trigger "FlowCompleteJob"
    When notification with key "EVENT_ID" and value "none" is added to "FlowCompleteSubscription"
    And condition with jmespath "confidenceLevels[0].value=='SUCCESS'" is added to "FlowCompleteSubscription"
    And condition with jmespath "publications[0].locations[0].type=='ARTIFACTORY'" is added to "FlowCompleteSubscription"
    And condition with jmespath "testCaseExecutions[0].outcome.verdict=='PASSED'" is added to "FlowCompleteSubscription"
    And condition with jmespath "testCaseExecutions[0].testCaseStartedTime" is added to "FlowCompleteSubscription"
    Then we send the "FlowCompleteSubscription" to eiffel intelligence for creation.

    #####Check so that everything triggers######
    When the jenkins job "ArtC2Job" is triggered
    Then all jenkins jobs has been triggered
    And subscriptions and jenkins jobs should be removed
