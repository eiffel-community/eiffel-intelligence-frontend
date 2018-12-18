@AuthNoSecurityFeature
Feature: Authentication with no security test

  @AuthCheckSecurityScenario
  Scenario: Check Security
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/auth'
    And request is sent
    Then response code 200 is received
    And response body '{"security":false}' is received

  @AuthLoginScenario
  Scenario: Login
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/auth/login'
    And request is sent
    Then response code 200 is received
    And response body '{"user":"anonymousUser"}' is received

  @AuthLogoutScenario
  Scenario: Logout
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/auth/logout'
    And request is sent
    Then response code 404 is received

  @AuthCheckStatusScenario
  Scenario: Check Status
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/auth/checkStatus'
    And request is sent
    Then response code 200 is received
    And response body 'Backend server is up and running' is received