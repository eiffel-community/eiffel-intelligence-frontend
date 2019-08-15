@AuthFeature
Feature: Authentication test

  @AuthCheckSecurityScenario
  Scenario: Check security
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/auth'
    And request is sent
    Then response code 200 is received
    And response body '{"security":true}' is received

  @AuthCheckStatusScenario
  Scenario: Check status test
    Given frontend is up and running
    When a 'GET' request is prepared for REST API '/auth/checkStatus'
    And username "gauss" and password "password" is used as credentials
    And request is sent
    Then response code 200 is received
    And response body 'Backend server is up and running' is received

  @AuthMultipleUsersLoginAndLogoutScenario
  Scenario: Multiple Users Login And Logout test
    Given frontend is up and running

    # Action: First user logging in
    When a 'GET' request is prepared for REST API '/auth/login'
    And username "gauss" and password "password" is used as credentials
    Then request is saved to request list at index 0
    When request is performed from request list at index 0
    Then response code 200 is received
    And response body '{"user":"gauss"}' is received
    And remove 'Authorization' from request headers at list index 0

    # Action: Second user logging in
    When a 'GET' request is prepared for REST API '/auth/login'
    And username "newton" and password "password" is used as credentials
    Then request is saved to request list at index 1
    When request is performed from request list at index 1
    Then response code 200 is received
    And response body '{"user":"newton"}' is received
    And remove 'Authorization' from request headers at list index 1

    # Check: First user is still logged in
    When request is performed from request list at index 0
    Then response code 200 is received
    And response body '{"user":"gauss"}' is received

    # Action: First user logging out
    When '/auth/logout' endpoint is set in request list at index 0
    And request is performed from request list at index 0
    Then response code 204 is received

    # Check: Second user is still logged in
    When request is performed from request list at index 1
    Then response code 200 is received
    And response body '{"user":"newton"}' is received

    # Check: First user proved logged out
    When '/auth/login' endpoint is set in request list at index 0
    And request is performed from request list at index 0
    Then response code 401 is received

    # Action: Second user logging out
    When '/auth/logout' endpoint is set in request list at index 1
    And request is performed from request list at index 1
    Then response code 204 is received

    # Check: Second user proved logged out
    When '/auth/login' endpoint is set in request list at index 1
    And request is performed from request list at index 1
    Then response code 401 is received

  @AuthInvalidUserOrPassword
  Scenario: Check invalid user or password test
    Given frontend is up and running
    
    # Invalid Username
    When a 'GET' request is prepared for REST API '/auth/checkStatus'
    And username "invalid_username" and password "password" is used as credentials
    And request is sent
    Then response code 401 is received
    
    # Invalid Password
    When a 'GET' request is prepared for REST API '/auth/checkStatus'
    And username "gauss" and password "invalid_password" is used as credentials
    And request is sent
    Then response code 401 is received

  @AuthUniqueUsersInDifferentLDAPServers
  Scenario: Login using unique users from two different LDAP servers
    # Action: First user logging in on first LDAP
    When a 'GET' request is prepared for REST API '/auth/login'
    And username "gauss" and password "password" is used as credentials
    Then request is saved to request list at index 0
    When request is performed from request list at index 0
    Then response code 200 is received
    And response body '{"user":"gauss"}' is received

    # Action: First user logging out
    When '/auth/logout' endpoint is set in request list at index 0
    And request is performed from request list at index 0
    Then response code 204 is received

    # Action: Second user logging in on second LDAP
    When a 'GET' request is prepared for REST API '/auth/login'
    And username "einstein" and password "password" is used as credentials
    Then request is saved to request list at index 0
    When request is performed from request list at index 0
    Then response code 200 is received
    And response body '{"user":"einstein"}' is received

    # Action: Second user logging out
    When '/auth/logout' endpoint is set in request list at index 0
    And request is performed from request list at index 0
    Then response code 204 is received

  @AuthIdenticalUsernamesInDifferentLDAPServers
  Scenario: Login using identical usernames with different passwords from two different LDAP servers
    # Action: User logging in on first LDAP
    When a 'GET' request is prepared for REST API '/auth/login'
    And username "newton" and password "password" is used as credentials
    Then request is saved to request list at index 0
    When request is performed from request list at index 0
    Then response code 200 is received
    And response body '{"user":"newton"}' is received

    # Action: User logging out
    When '/auth/logout' endpoint is set in request list at index 0
    And request is performed from request list at index 0
    Then response code 204 is received

    # Action: User logging in on second LDAP
    When a 'GET' request is prepared for REST API '/auth/login'
    And username "newton" and password "password2" is used as credentials
    Then request is saved to request list at index 0
    When request is performed from request list at index 0
    Then response code 200 is received
    And response body '{"user":"newton"}' is received