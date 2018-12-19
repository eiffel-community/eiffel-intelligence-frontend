@InformationFeature
Feature: Information test

  @GetInformationScenario
  Scenario: Get Information
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/information'
    And request is sent
    Then response code 200 is received