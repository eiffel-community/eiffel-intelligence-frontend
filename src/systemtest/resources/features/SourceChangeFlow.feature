@SourceChangeFlowFeature
Feature: Source Change system test

  @SourceChangeFlowScenario
  Scenario: Creates jenkins jobs, subscriptions and at last see that the last job has been
            triggered in order to know if all events has been aggregated correctly.

    Given configurations are provided
    Then a jenkins job '"SCS1Job"' from '"src/systemtest/resources/JenkinsShellScripts/SCS1Script.txt"' is created with parameters: ,
    And a jenkins job '"ActT1Job"' from '"src/systemtest/resources/JenkinsShellScripts/ActT1Script.txt"' is created with parameters: EVENT_ID,
    And a jenkins job '"ActSFCJob"' from '"src/systemtest/resources/JenkinsShellScripts/ActSFCScript.txt"' is created with parameters: EVENT_ID,
    And a jenkins job '"CLMJob"' from '"src/systemtest/resources/JenkinsShellScripts/CLMScript.txt"' is created with parameters: EVENT_ID,
    And a jenkins job '"FlowCompleteJob"' from '"src/systemtest/resources/JenkinsShellScripts/FlowCompleteScript.txt"' is created with parameters: ,

    ### Add CLMSubscription to EI ###
    Given subscription object "CLMSubscription" is created which will trigger "CLMJob" with parameters
    When notification with key "EVENT_ID" and value "id" is added to "CLMSubscription"
    And condition with jmespath "submission.gitIdentifier.repoName=='my-repo'" is added to "CLMSubscription"
    And condition with jmespath "submission.submitter.email=='jane@example.com'" is added to "CLMSubscription"
    Then we send the "CLMSubscription" to eiffel intelligence for creation.

    ### Add ActT1Subscription to EI ###
    Given subscription object "ActT1Subscription" is created which will trigger "ActT1Job" with parameters
    When notification with key "EVENT_ID" and value "submission.confidenceLevels[0].eventId" is added to "ActT1Subscription"
    And condition with jmespath "submission.confidenceLevels[?value=='SUCCESS']" is added to "ActT1Subscription"
    Then we send the "ActT1Subscription" to eiffel intelligence for creation.

    ### Add ActSFCSubscription to EI ###
    Given subscription object "ActSFCSubscription" is created which will trigger "ActSFCJob" with parameters
    When notification with key "EVENT_ID" and value "causedActivities[0].triggeredEventId" is added to "ActSFCSubscription"
    And condition with jmespath "causedActivities[?name=='TriggeredOnSourceChangeSubmitted']" is added to "ActSFCSubscription"
    Then we send the "ActSFCSubscription" to eiffel intelligence for creation.

    ### Add FlowCompleteSubscription to EI ###
    Given subscription object "FlowCompleteSubscription" is created which will trigger "FlowCompleteJob"
    And condition with jmespath "creations[?author.name=='Jane Doe']" is added to "FlowCompleteSubscription"
    And condition with jmespath "submission.gitIdentifier.repoName=='my-repo'" is added to "FlowCompleteSubscription"
    And condition with jmespath "submission.confidenceLevels[?value=='SUCCESS']" is added to "FlowCompleteSubscription"
    And condition with jmespath "causedActivities[?outcome.conclusion=='SUCCESSFUL']" is added to "FlowCompleteSubscription"
    Then we send the "FlowCompleteSubscription" to eiffel intelligence for creation.

    ### Check everything has been triggered ###
    When the jenkins job "SCS1Job" is triggered
    Then all jenkins jobs has been triggered
    And subscriptions and jenkins jobs should be removed
