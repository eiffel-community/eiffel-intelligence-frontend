@DownloadFeature
Feature: Download test

  @GetSubscriptionsTemplateScenario
  Scenario: Get subscriptions template
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/download/subscriptions-template'
    And request is sent
    Then response code 200 is received
    And response body contains 'Subscription1'

  @GetRulesTemplateScenario
  Scenario: Get rules template
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/download/rules-template'
    And request is sent
    Then response code 200 is received
    And response body contains 'ARTIFACT_TEST'

  @GetEventsTemplateScenario
  Scenario: Get events template
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/download/events-template'
    And request is sent
    Then response code 200 is received
    And response body contains 'EiffelArtifactCreatedEvent'