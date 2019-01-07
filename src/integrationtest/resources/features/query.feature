@QueryFeature
Feature: Query test

  @QueryMissedNotificationsScenario
  Scenario: Query missed notifications
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/queryMissedNotifications'
    And param key 'SubscriptionName' with value 'NonExistingSubscription' is added
    And request is sent
    Then response code 200 is received
    And response body '{"responseEntity":"[]"}' is received

  @QueryByIdScenario
  Scenario: Query aggregated object by ID
    Given frontend is up and running
    And an aggregated object is created
    When a 'GET' request is prepared for REST API '/queryAggregatedObject'
    And param key 'ID' with value '6acc3c87-75e0-4b6d-88f5-b1a5d4e62b43' is added
    And request is sent
    Then response code 200 is received
    And body contains '6acc3c87-75e0-4b6d-88f5-b1a5d4e62b43'

  @QueryFreestyleScenario
  Scenario: Query aggregated object with freestyle criteria
    Given frontend is up and running
    And an aggregated object is created
    When a 'POST' request is prepared for REST API '/query'
    And body is set to file 'queryFreestyle.json'
    And request is sent
    Then response code 200 is received
    And body contains '6acc3c87-75e0-4b6d-88f5-b1a5d4e62b43'