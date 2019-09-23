@StatusFeature
Feature: Status test

  @StatusScenario
  Scenario: Check status test
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/status'
    And request is sent
    Then response code 200 is received
    And response body contains '"eiffelIntelligenceStatus" : "AVAILABLE"'
    And response body contains '"rabbitMQStatus" : "AVAILABLE"'
    And response body contains '"mongoDBStatus" : "AVAILABLE"'
    