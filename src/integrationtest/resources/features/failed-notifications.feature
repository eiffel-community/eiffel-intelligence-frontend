@FailedNotificationsFeature
Feature: Failed notifications test

  @FailedNotificationsScenario
  Scenario: Query failed notifications
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/failed-notifications'
    And param key 'subscriptionNames' with value 'NonExistingSubscription' is added
    And request is sent
    Then response code 200 is received
    And response body '[]' is received