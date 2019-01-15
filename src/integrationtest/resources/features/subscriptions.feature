@SubscriptionsFeature
Feature: Subscriptions test

  @AddSubscriptionScenario
  Scenario: Add subscription
    Given frontend is up and running
    When a 'POST' request is prepared for REST API '/subscriptions'
    And body is set to file 'subscription_single.json'
    And username "gauss" and password "password" is used as credentials
    And request is sent
    Then response code 200 is received
    
  @ModifySubscriptionScenario
  Scenario: Modify subscription
    Given frontend is up and running
    When a 'PUT' request is prepared for REST API '/subscriptions'
    And body is set to file 'subscription_single_modify.json'
    And username "gauss" and password "password" is used as credentials
    And request is sent
    Then response code 200 is received

  @GetSubscriptionScenario
  Scenario: Get subscription
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/subscriptions'
    And '/Subscription_Test' is appended to endpoint
    And username "gauss" and password "password" is used as credentials
    And request is sent
    Then response code 200 is received
    And response body contains 'MAIL'

  @DeleteSubscriptionScenario
  Scenario: Delete subscription
    Given frontend is up and running
    When a 'DELETE' request is prepared for REST API '/subscriptions'
    And '/Subscription_Test' is appended to endpoint
    And username "gauss" and password "password" is used as credentials
    And request is sent
    Then response code 200 is received