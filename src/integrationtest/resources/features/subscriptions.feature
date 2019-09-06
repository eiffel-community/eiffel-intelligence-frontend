@SubscriptionsFeature
Feature: Subscriptions test

  @AddSubscriptionNoAuthScenario
  Scenario: Add subscription
    Given frontend is up and running
    When a 'POST' request is prepared for REST API '/subscriptions'
    And body is set to file 'subscription_multi.json'
    And request is sent
    Then response code 401 is received

  @AddSubscriptionScenario
  Scenario: Add subscription
    Given frontend is up and running
    When a 'POST' request is prepared for REST API '/subscriptions'
    And body is set to file 'subscription_multi.json'
    And username "gauss" and password "password" is used as credentials
    And request is sent
    Then response code 200 is received

  @ModifySubscriptionNoAuthScenario
  Scenario: Modify subscription
    Given frontend is up and running
    When a 'PUT' request is prepared for REST API '/subscriptions'
    And body is set to file 'subscription_modify.json'
    And request is sent
    Then response code 401 is received

  @ModifySubscriptionScenario
  Scenario: Modify subscription
    Given frontend is up and running
    When a 'PUT' request is prepared for REST API '/subscriptions'
    And body is set to file 'subscription_modify.json'
    And username "gauss" and password "password" is used as credentials
    And request is sent
    Then response code 200 is received

  @GetSubscriptionScenario
  Scenario: Get subscription
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/subscriptions'
    And '/Subscription_IT' is appended to endpoint
    And request is sent
    Then response code 200 is received
    And response body contains 'MAIL'

  @GetSubscriptionMultiScenario
  Scenario: Get subscription
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/subscriptions'
    And param key 'subscriptionNames' with value 'Subscription_IT,Subscription_IT_3' is added
    And request is sent
    Then response code 200 is received
    And response body does not contain 'Subscription_IT_2'

  @GetSubscriptionAllScenario
  Scenario: Get subscription
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/subscriptions'
    And request is sent
    Then response code 200 is received
    And response body contains 'Subscription_IT'
    And response body contains 'Subscription_IT_2'
    And response body contains 'Subscription_IT_3'

  @DeleteSubscriptionNoAuthScenario
  Scenario: Delete subscription
    Given frontend is up and running
    When a 'DELETE' request is prepared for REST API '/subscriptions'
    And '/Subscription_IT' is appended to endpoint
    And request is sent
    Then response code 401 is received

  @DeleteSubscriptionScenario
  Scenario: Delete subscription
    Given frontend is up and running
    When a 'DELETE' request is prepared for REST API '/subscriptions'
    And '/Subscription_IT' is appended to endpoint
    And username "gauss" and password "password" is used as credentials
    And request is sent
    Then response code 200 is received

  @DeleteSubscriptionMultiScenario
  Scenario: Delete subscription
    Given frontend is up and running
    When a 'DELETE' request is prepared for REST API '/subscriptions'
    And param key 'subscriptionNames' with value 'Subscription_IT_2,Subscription_IT_3' is added
    And username "gauss" and password "password" is used as credentials
    And request is sent
    Then response code 200 is received