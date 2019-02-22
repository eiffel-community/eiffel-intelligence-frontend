@SourceChangeFlowFeature
Feature: Source Change system test

  @SourceChangeFlowScenario
  Scenario: Creates jenkins jobs, subscriptions and at last see that the last job has been
            triggered in order to know if all events has been aggregated correctly.

    Given configurations are provided

    # A bunch of Jenkins job will be created here
    Then a jenkins job '"SCS1Job"' from '"src/systemtest/resources/JenkinsShellScripts/SCS1Script.txt"' is created with parameters: ,
    And a jenkins job '"ActT1Job"' from '"src/systemtest/resources/JenkinsShellScripts/ActT1Script.txt"' is created with parameters: EVENT_ID,
    And a jenkins job '"ActSFCJob"' from '"src/systemtest/resources/JenkinsShellScripts/ActSFCScript.txt"' is created with parameters: EVENT_ID,
    And a jenkins job '"CLMJob"' from '"src/systemtest/resources/JenkinsShellScripts/CLMScript.txt"' is created with parameters: EVENT_ID,

    # Subscriptions will be created here


    # Check everything has been triggered
    And subscriptions and jenkins jobs should be removed
