@QueryFeature
Feature: Query test

  @QueryByIdScenario
  Scenario: Query aggregated object by ID
    Given frontend is up and running
    And an aggregated object is created
    When a 'GET' request is prepared for REST API '/aggregated-objects'
    And '/6acc3c87-75e0-4b6d-88f5-b1a5d4e62b43' is appended to endpoint
    And request is sent for 30 seconds until response code no longer matches 204
    And response body contains '6acc3c87-75e0-4b6d-88f5-b1a5d4e62b43'

  @QueryFreestyleScenario
  Scenario: Query aggregated object with freestyle criteria
    Given frontend is up and running
    And an aggregated object is created
    When a 'POST' request is prepared for REST API '/aggregated-objects/query'
    And body is set to file 'queryFreestyle.json'
    And request is sent for 30 seconds until response code no longer matches 204
    And response body contains '6acc3c87-75e0-4b6d-88f5-b1a5d4e62b43'