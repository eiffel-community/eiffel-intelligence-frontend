@AuthFeature
Feature: Authentication test

  @AuthCheckSecurityScenario
  Scenario: Check security
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/auth'
    And request is sent
    Then response code 200 is received
    And response body '{"security":true}' is received

  @AuthLoginScenario
  Scenario: Login test
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/auth/login'
    And username "gauss" and password "password" is used as credentials
    And request is sent
    Then response code 200 is received
    And response body '{"user":"gauss"}' is received

  @AuthLogoutScenario
  Scenario: Logout test
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/auth/logout'
    And request is sent
    Then response code 204 is received

  @AuthCheckStatusScenario
  Scenario: Check status test
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/auth/checkStatus'
    And username "gauss" and password "password" is used as credentials
    And request is sent
    Then response code 200 is received
    And response body 'Backend server is up and running' is received