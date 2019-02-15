@AuthFeature
Feature: Artifact system test

  @ArtifactFlowScenario
  Scenario: Artifact Flow Scenario
    Given configurations are provided
    When a jenkins job '"ArtC2Job"' from '"src/systemtest/resources/JenkinsShellScripts/ArtC2Script.txt"' is created
    And a jenkins job '"TCTJob"' from '"src/systemtest/resources/JenkinsShellScripts/TCTScript.txt"' is created
    And a jenkins job '"TCSTCFJob"' from '"src/systemtest/resources/JenkinsShellScripts/TCSTCFScript.txt"' is created
    And a jenkins job '"CLMJob"' from '"src/systemtest/resources/JenkinsShellScripts/CLMScript.txt"' is created
    And a jenkins job '"ArtPJob"' from '"src/systemtest/resources/JenkinsShellScripts/ArtPScript.txt"' is created
    Then jenkins is set up with the following jobs
    | ArtC2Job  |
    | TCTJob    |
    | TCSTCFJob |
    | CLMJob    |
    | ArtPJob   |

    Given subscription "CLMSubscription" is created which will trigger "CLMJob"
    When notification with key "EVENT_ID" and value "id" is added to "CLMSubscription"
    And notification with key "TEST_CASE_NAME" and value "'Test1'" is added to "CLMSubscription"
    Then we send the "CLMSubscription" to eiffel intelligence for creation.
    And subscriptions and jenkins jobs should be removed

