# EI Frontend Authentication Documentation

## Overview

This documentation explains how to authenticate with the EI Frontend using different authentication mechanisms for human-based (H2M) and machine-to-machine (M2M) scenarios.

Authentication is not a requirement but can be turned on and off in the
application properties file with the 'spring.cloud.azure.active-directory.enabled' property.

### Authentication Types:

- **H2M Authentication**: For human-based authentication, users will be redirected to the Microsoft login page for Single Sign-On (SSO). Multi-Factor Authentication (MFA) may be prompted if required.
  
- **M2M Authentication**: In M2M scenarios, the client application directly makes a `GET` request to the `/authentication/login` API. The client must provide a username and password using basic authentication. The response headers will includes two tokens:
  - `xauth-token`: Required for making requests to the `/subscriptions` API (POST, PUT, DELETE).
  - `azure-token`: Required for accessing all APIs when azure authentication is enabled.

**Note**: Include both xauth-token and azure-token in the headers for accessing `/subscriptions` API (POST, PUT, DELETE).

## Step 1: Authenticating to Obtain Tokens

To authenticate and receive the necessary tokens, make a `GET` request to the `/authentication/login` endpoint.

### Request Example:

    curl -X GET -H "Content-Type: application/json" -u <user>:<password> http://localhost:8080/authentication/login

Example of full response

    < HTTP/1.1 200 
    < Vary: Origin
    < Vary: Access-Control-Request-Method
    < Vary: Access-Control-Request-Headers
    < Set-Cookie: JSESSIONID=4AC3653506A32762E220489C8230E37F; Path=/; HttpOnly
    < X-Auth-Token: 0c61a3d0-a154-42ac-ad08-aea6fda9de9a
    < azure-token: eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6ImltaTBZMnowthlLeEJ0dEFxS19UdDVoWUJUayIsImtpZCI6ImltaTBZMnowZFlLeEJ0dEFxS19UdDVoWUJUayJ9.eyJhdWQiOiJhcGk6Ly83Mzg3N2ViNS1iNjUxLTRjYzAtYjI5Yi05NTYyZGRjMjgyYzciLCJpc3MiOiJodHRwczovL3N0cy53aW5kb3dzLm5ldC85MmU4NGNlYi1mYmZkLTQ3YWItYmU1Mi0wODBjNmI4Nzk1M2YvIiwiaWF0IjoxNzQwOTc2MzY5LCJuYmYiOjE3NDA5NzYzNjksImV4cCI6MTc0MDk4MDI2OSwiYWlvIjoiazJSZ1lEaktPNWR6cTU3aWh2bDgyMlkybU90cis1KzVFN0h6eTJ2aHU5WXRSVUhpb1Z3QSIsImFwcGlkIjoiNzM4NzdlYjUtYjY1MS00Y2MwLWIyOWItOTU2MmRkYzI4MmM3IiwiYXBwaWRhY3IiOiIxIiwiaWRwIjoiaHR0cHM6Ly9zdHMud2luZG93cy5uZXQvOTJlODRjZWItZmJmZC00N2FiLWJlNTItMDgwYzZiODc5NTNmLyIsIm9pZCI6IjBhMGRhODg0LTZhZmEtNGIwNC05NmIyLTVhYjJjODIxYjI0NCIsInJoIjoiMS5BUkVBNjB6b2t2MzdxMGUtVWdnTWE0ZVZQN1YtaDNOUnRzQk1zcHVWWXQzQ2dzY1JBQUFSQUEuIiwicm9sZXMiOlsiYXBpLnJlYWQiXSwic3ViIjoiMGEwZGE4ODQtNmFmYS00YjA0LTk2YjItNWFiMmM4MjFiMjQ0IiwidGlkIjoiOTJlODRjZWItZmJmZC00N2FiLWJlNTItMDgwYzZiODc5NTNmIiwidXRpIjoidURfa2htWHNJRWVEQUsyakFnd3hBQSIsInZlciI6IjEuMCJ9.IpUvwU7OA1x9B3OaE24QDRH0FXKfmo5dHJ1uygLb2sAsRzxHMOTbnerqKnggAqvDuFAQL4Cp3Cn6OINlA6Az8OoyxQwQVtXCb52fElik5N8HbjNhM6YVmC6lIRem0rm8W2SmGQad0gAhEgO_QdCDYQJP3_9EgFxuqjA2nyWNPzM7hZzniiAIm1eokRIb92Nb05mBirj9V2bs8viVnS--0c7P2nF6L8EfE2KeeHg675WsMuA9Gv8MOyxhMl5-H3Ri86m7Nx2G1rwMNQA3QwP-cOgjIDG5nNGvxjbDpycQSxl_qJS19qjpa3y6QhbcpnNZ3jf7_Se_LNyy-H4O6i2tmg
    < X-Content-Type-Options: nosniff
    < X-XSS-Protection: 1; mode=block
    < Cache-Control: no-cache, no-store, max-age=0, must-revalidate
    < Pragma: no-cache
    < Expires: 0
    < X-Frame-Options: DENY
    < Content-Type: application/json
    < Content-Length: 18
    < Date: Mon, 03 Mar 2025 04:37:49 GMT
    < 
    * Connection #0 to host localhost left intact
    {"user":"myuser"}

## Step 2: Making API Requests with Tokens

Once you have obtained the tokens, include them in your API requests as follows:

### 1. Accessing All APIs (e.g., `/status`, `/backends`, `/templates`, etc.)

For all APIs include the `Authorization` header with the `azure-token`.

#### Request Example for GET

    curl -X GET -H "Authorization: Bearer <azure-token>" http://localhost:8080/status
    
### 2. Accessing subscriptions APIs (e.g., `/subscriptions`)

    curl -X POST -d @file_containing_list_of_json_objects -H "Content-Type: application/json" \
        -H "X-Auth-Token: <xauth-token>" -H "Authorization: Bearer <azure-token> \
        http://localhost:8080/subscriptions

**More information about how to make API Requests can be found [here](https://github.com/eiffel-community/eiffel-intelligence-frontend/blob/master/wiki/curl-examples.md).**


