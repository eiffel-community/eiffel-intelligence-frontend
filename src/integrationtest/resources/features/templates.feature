@TemplatesFeature
Feature: Test get templates

  @GetSubscriptionsTemplateScenario
  Scenario: Get subscriptions template
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/templates/subscriptions'
    And request is sent
    Then response code 200 is received
    And response body contains 'Subscription1'

  @GetRulesTemplateScenario
  Scenario: Get rules template
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/templates/rules'
    And request is sent
    Then response code 200 is received
    And response body contains 'ARTIFACT_TEST'

  @GetEventsTemplateScenario
  Scenario: Get events template
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/templates/events'
    And request is sent
    Then response code 200 is received
    And response body contains 'EiffelArtifactCreatedEvent'