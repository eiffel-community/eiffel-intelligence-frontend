@AuthFeature
Feature: Artifact system test

  @ArtifactFlowScenario
  Scenario: Artifact Flow Scenario
    Given configurations are provided
    And some subscriptions are set up
    And a jenkins job '"ArtC2Job"' from '"src/systemtest/resources/JenkinsShellScripts/ArtC2Script.txt"' is created
    And a jenkins job '"TCTJob"' from '"src/systemtest/resources/JenkinsShellScripts/TCTScript.txt"' is created
    And a jenkins job '"TCS&TCTJob"' from '"src/systemtest/resources/JenkinsShellScripts/TCS&TCTScript.txt"' is created
    And a jenkins job '"CLMJob"' from '"src/systemtest/resources/JenkinsShellScripts/CLMScript.txt"' is created
    And a jenkins job '"ArtP"' from '"src/systemtest/resources/JenkinsShellScripts/ArtPScript.txt"' is created
    When next story happens
    Then all is good

