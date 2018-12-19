@RulesFeature
Feature: Rules test

  @RulesPageEnabledScenario
  Scenario: Rules page enabled
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/rules/rule-check/testRulePageEnabled'
    And request is sent
    Then response code 200 is received
    And response body '{"status":true}' is received

  @RulesAggregationScenario
  Scenario: Rules aggregation
    Given frontend is up and running
    When a 'POST' request is prepared for REST API '/rules/rule-check/aggregation'
    And aggregation is prepared with rules file 'listRules.json' and events file 'listEvents.json'
    And request is sent
    Then response code 200 is received
    And response body from file 'aggregationResult.json' is received