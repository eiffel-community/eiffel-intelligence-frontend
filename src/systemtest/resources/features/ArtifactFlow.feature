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
    And a jenkins job "FlowCompleteJob" from "src/systemtest/resources/JenkinsShellScripts/FlowCompleteScript.txt" is created with parameters: ,

    #####Add TCT1Subscription to EI#####
    Given subscription object "TCT1Subscription" is created which will trigger "TCTJob" with parameters
    When notification with key "EVENT_ID" and value "id" is added to "TCT1Subscription"
    And notification with key "TEST_CASE_NAME" and value "'Test1'" is added to "TCT1Subscription"
    And condition with jmespath "gav.artifactId=='ArtC2'" is added to "TCT1Subscription"
    Then we send the "TCT1Subscription" to eiffel intelligence for creation.

    #####Add TCT2Subscription to EI#####
    Given subscription object "TCT2Subscription" is created which will trigger "TCTJob" with parameters
    When notification with key "EVENT_ID" and value "id" is added to "TCT2Subscription"
    And notification with key "TEST_CASE_NAME" and value "'Test2'" is added to "TCT2Subscription"
    And condition with jmespath "gav.artifactId=='ArtC2'" is added to "TCT2Subscription"
    Then we send the "TCT2Subscription" to eiffel intelligence for creation.

    #####Add TCSTCF1Subscription to EI#####
    Given subscription object "TCSTCF1Subscription" is created which will trigger "TCSTCFJob" with parameters
    When notification with key "EVENT_ID" and value "testCaseExecutions[?outcome.id =='Test1'] | [0].testCaseTriggeredEventId" is added to "TCSTCF1Subscription"
    And condition with jmespath "testCaseExecutions[?outcome.id =='Test1']" is added to "TCSTCF1Subscription"
    Then we send the "TCSTCF1Subscription" to eiffel intelligence for creation.

    #####Add TCSTCF2Subscription to EI#####
    Given subscription object "TCSTCF2Subscription" is created which will trigger "TCSTCFJob" with parameters
    When notification with key "EVENT_ID" and value "testCaseExecutions[?outcome.id =='Test2'] | [0].testCaseTriggeredEventId" is added to "TCSTCF2Subscription"
    And condition with jmespath "testCaseExecutions[?outcome.id =='Test2']" is added to "TCSTCF2Subscription"
    Then we send the "TCSTCF2Subscription" to eiffel intelligence for creation.

    #####Add CLMSubscription to EI#####
    Given subscription object "CLMSubscription" is created which will trigger "CLMJob" with parameters
    When notification with key "EVENT_ID" and value "id" is added to "CLMSubscription"
    And condition with jmespath "testCaseExecutions[?outcome.id =='Test1'] | [0].outcome.verdict=='PASSED'" is added to "CLMSubscription"
    And condition with jmespath "testCaseExecutions[?outcome.id =='Test2'] | [0].outcome.verdict=='PASSED'" is added to "CLMSubscription"
    Then we send the "CLMSubscription" to eiffel intelligence for creation.

    #####Add ArtPSubscription to EI#####
    Given subscription object "ArtPSubscription" is created which will trigger "ArtPJob" with parameters
    When notification with key "EVENT_ID" and value "id" is added to "ArtPSubscription"
    And condition with jmespath "gav.artifactId=='ArtC2'" is added to "ArtPSubscription"
    Then we send the "ArtPSubscription" to eiffel intelligence for creation.

    #####Add FlowCompleteSubscription#####
    Given subscription object "FlowCompleteSubscription" is created which will trigger "FlowCompleteJob"
    And condition with jmespath "confidenceLevels[?name=='unittest'] | [0].value == 'SUCCESS'" is added to "FlowCompleteSubscription"
    And condition with jmespath "publications[?locations[?type=='ARTIFACTORY']]" is added to "FlowCompleteSubscription"
    Then we send the "FlowCompleteSubscription" to eiffel intelligence for creation.

    #####Check so that everything triggers######
    When the jenkins job "ArtC2Job" is triggered
    Then all jenkins jobs has been triggered
    And subscriptions and jenkins jobs should be removed
