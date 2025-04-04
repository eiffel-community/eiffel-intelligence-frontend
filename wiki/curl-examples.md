# Curl Examples to Front-end

## Introduction

The front-end has a bridge functionality that is built in as a part of the front-end's web server 
and handles requests coming from the web GUI towards any of the configured Eiffel Intelligence back-ends. 
The `/backends` endpoint is the only additional endpoint which does not exist in [Eiffel Intelligence back-end](https://github.com/eiffel-community/eiffel-intelligence).
The front-end may also be used by other tools such as CURL or any kind of program that can make HTTP(S) 
requests. Below are some examples of using CURL towards different endpoints, together with example 
responses. Most endpoints are also documented in the [Eiffel Intelligence back-end repository](https://github.com/eiffel-community/eiffel-intelligence/tree/master/wiki)

**Note**: Please include required authentication tokens in headers only when authentication is enabled. If no authentication is used then need not require any authentication token while making Api requests. 

**Note**: If you have multiple url parameters, you need to add quotation mark around the entire query. For example:

    curl -X GET -H "Authorization: Bearer <azure-token> "http://localhost:8080/endpoint?varible1=1&varible2=2"

### Quick Access to Endpoints:
* [/authentication](#authentication)
* [/status](#status)
* [/backends](#backends)
* [/templates](#templates)
* [/information](#information)
* [/aggregated-objects](#aggregated-objects)
* [/failed-notifications](#failed-notifications)
* [/rules](#rules)
* [/subscriptions](#subscriptions)

### Specified Back-end
As a user of the front-end you may want to specify your own back-end URL if you do not want to use the 
default back-end. This is possible to do by injecting the back-end URL as a query parameter. The 
parameters key should be `backendurl` then enter the full HTTP URL you wish to use. This back-end 
instance does not have to be specified in the list of available instances.

An example of a way to add such parameter is exemplified below, note that the "?" indicates that 
parameters has been added to the front-end url.`localhost:8080` is the front-end url, and we want to 
access the context path /authentication but on URL `http://127.0.0.1:8090/` that is the back-end we wish to use.

    curl -X GET -H "Authorization: Bearer <azure-token> http://localhost:8080/authentication?backendurl="http://127.0.0.1:8090/"

Example with Eiffel Intelligence front-end deployed with Tomcat, and the 
context path /eifrontend/:

    curl -X GET -H "Authorization: Bearer <azure-token> http://localhost:8080/eifrontend/authentication?backendurl="http://127.0.0.1:8090/eibackend/"

This way of entering the `backendurl` may be the easiest way. It works with all CRUD operations. 

**Note: It is not possible to add the `backendurl` parameter as a JSON parameter.**

## <a id="authentication" /> /authentication

<table>
    <tr>
        <th>Endpoint</th>
        <th>Type</th>
        <th>Explanation</th>
    </tr>
    <tr>
        <td>/authentication</td>
        <td>GET</td>
        <td>Check if LDAP security is enabled</td>
    </tr>
    <tr>
        <td>/authentication/login</td>
        <td>GET</td>
        <td>Login to EI with a username and password</td>
    </tr>
    <tr>
        <td>/authentication/logout</td>
        <td>GET</td>
        <td>Logout from EI</td>
    </tr>
</table>

A curl request with the command:

    curl -X GET http://localhost:8080/authentication

Gives the response data `{"security":false}`. The `security` parameter would be true if LDAP was enabled.

It is also possible to login and logout using curl. The below command provides the `-u` flag followed by a username.

    curl -X GET -H "Content-type: application/json" -u <user> localhost:8080/authentication/login

It is possible to provide both username and password directly in the request, as seen below.

    curl -X GET -H "Content-type: application/json" -u <user>:<password> localhost:8080/authentication/login

More information and examples can be found in the [EI back-end documentation](https://github.com/eiffel-community/eiffel-intelligence/blob/master/wiki/authentication.md)

## <a id="status" /> /status

<table>
    <tr>
        <th>Endpoint</th>
        <th>Type</th>
        <th>Explanation</th>
    </tr>
    <tr>
        <td>/status</td>
        <td>GET</td>
        <td>Get back-end status including status of services back-end is dependent on.</td>
    </tr>
</table>

#### Curl Examples
This command would return a JSON object containing the status of the back-end and servers back-end is dependent on..

    curl -X GET -H "Content-type: application/json" -H "Authorization: Bearer <azure-token> http://localhost:8080/status

## <a id="backends" /> /backends

<table>
    <tr>
        <th>Endpoint</th>
        <th>Type</th>
        <th>Explanation</th>
    </tr>
    <tr>
        <td>/backends</td>
        <td>GET</td>
        <td>Retrieves a json list of available back-end instances</td>
    </tr>
</table>

While the web GUI may use the back-end instances list and select different back-ends in an easy way, 
a user that uses the front-end without the included web GUI may need to specify a back-end URL. This 
may be done by adding a `backendurl` parameter to the request.

Note that for users where the front-end and back-end is deployed with Tomcat there will be context paths used.

### Default Back-end

The front-end can be configured to use a default back-end, this means that if no back-end is specified 
when making a HTTP request to the front-end the default back-end will be used. If you want to see a 
list of back-ends and see if there is a default back-end set, you may use the command:

###### Example GET Request Using Curl
    curl -X GET -H "Authorization: Bearer <azure-token> http://*front-end-url*/*context-path-if-any*/backends

###### Example GET Request Using Curl with Context Path
    curl -X GET -H "Authorization: Bearer <azure-token> http://localhost:8080/eifrontend/backends

###### Example GET Request Using Curl with Eiffel Intelligence Default Settings
    curl -X GET -H "Authorization: Bearer <azure-token> http://localhost:8080/backends

The default back-end should have the key `defaultBackend` set to `true`. If the JSON list ends up 
empty there are no back-ends specified in the front-end. If there is no JSON object with the key set 
to true there is no default back-end.

__Note:__ `name` must be unique.

Only one back-end instance can be added at a time. Even with different names all elements must be 
unique, you may not have two or more elements with the same `host`, `port`, `contextPath` or `https` 
value, one of these three keys must be different. Only the `contextPath` key may be left empty.

###### Example of Valid Back-end List:

    [
        {"name":"My Back-End","host":"localhost","port":8090,"contextPath":"","https":false,"defaultBackend":true},
        {"name":"My Back-End 2","host":"local-toast","port":8090,"contextPath":"","https":false,"defaultBackend":false},
        {"name":"My Back-End 3","host":"localhost","port":8091,"contextPath":"","https":false,"defaultBackend":false},
        {"name":"My Back-End 4","host":"localhost","port":8090,"contextPath":"/back-end","https":false,"defaultBackend":false},
        {"name":"My Back-End 5","host":"localhost","port":8090,"contextPath":"","https":true,"defaultBackend":false}
    ]

All entries has different `names`.
Second entry has different `host`, third entry has different `port`, third entry has different `contextPath`, 
fifth entry has `https` changed to true.

###### Example of Invalid Back-end List:

    [
        {"name":"My Back-End","host":"localhost","port":8090,"contextPath":"","https":false,"defaultBackend":true},
        {"name":"My Back-End","host":"local-toast","port":12345,"contextPath":"/my-path","https":true,"defaultBackend":false},
        {"name":"My Back-End 2","host":"localhost","port":8090,"contextPath":"","https":false,"defaultBackend":false}
    ]

The second entry is invalid due to having the same name as the first entry. The third entry is invalid 
due to having the same value in all fields as the first entry.

## <a id="templates" />/templates

<table>
    <tr>
        <th>Endpoint</th>
        <th>Type</th>
        <th>Explanation</th>
    </tr>
    <tr>
        <td>/templates/subscriptions</td>
        <td>GET</td>
        <td>Downloads a template with several predefined subscriptions</td>
    </tr>
    <tr>
        <td>/templates/rules</td>
        <td>GET</td>
        <td>Downloads a template with several predefined rules</td>
    </tr>
    <tr>
        <td>/templates/events</td>
        <td>GET</td>
        <td>Downloads a template with several predefined events</td>
    </tr>
</table>

The Eiffel Intelligence front-end supports these endpoints. More information can be found in the 
[Eiffel Intelligence back-end documentation](https://github.com/eiffel-community/eiffel-intelligence/blob/master/wiki/templates.md)

#### Curl Examples

This command would return a list of rules.

    curl -X GET -H "Content-type: application/json" -H "Authorization: Bearer <azure-token> http://localhost:8080/templates/rules

This command returns a list of predefined template events.

    curl -X GET -H "Content-type: application/json" -H "Authorization: Bearer <azure-token> http://localhost:8080/templates/events

This command returns a list of several subscriptions. It is also possible to specify a file in which 
to save the downloaded objects to.

    curl -X GET -H "Content-type: application/json" -H "Authorization: Bearer <azure-token> localhost:8080/templates/subscriptions --output myFile.json

## <a id="information" />/information

<table>
    <tr>
        <th>Endpoint</th>
        <th>Type</th>
        <th>Explanation</th>
    </tr>
    <tr>
        <td>/information</td>
        <td>GET</td>
        <td>Fetches all information about the back-end and its components</td>
    </tr>
</table>

This endpoint support `GET` requests, which returns information about EI back-end and connected components.
The response is a json object containing all the connected components and data about them.

    curl -X GET -H "Content-type: application/json" -H "Authorization: Bearer <azure-token> localhost:8080/information

## <a id="aggregated-objects" />/aggregated-objects

<table>
    <tr>
        <th>Endpoint</th>
        <th>Type</th>
        <th>Explanation</th>
    </tr>
    <tr>
        <td>/aggregated-objects/{id}</td>
        <td>GET</td>
        <td>query an aggregated object with its id</td>
    </tr> 
    <tr>
        <td>/aggregated-objects/query</td>
        <td>POST</td>
        <td>Free style query; user specified queries</td>
    </tr>    
</table>

Example curl commands to these endpoints [can be found here](https://github.com/eiffel-community/eiffel-intelligence/blob/master/wiki/query-aggregated-objects.md) 

## <a id="failed-notifications" />/failed-notifications

<table>
    <tr>
        <th>Endpoint</th>
        <th>Type</th>
        <th>Explanation</th>
      </tr>
      <tr>
        <td>/failed-notifications</td>
        <td>GET</td>
        <td>Queries any failed notifications</td>
      </tr>      
</table>

Example curl commands to these endpoints [can be found here](https://github.com/eiffel-community/eiffel-intelligence/blob/master/wiki/failed-notifications.md)

## <a id="rules" />/rules

<table>
    <tr>
        <th>Endpoint</th>
        <th>Type</th>
        <th>Explanation</th>
    </tr>
    <tr>
        <td>/rules</td>
        <td>GET</td>
        <td>Get the current rules content</td>
    </tr>
    <tr>
        <td>/rule-test</td>
        <td>GET</td>
        <td>Check if TestRules is enabled in the back-end</td>
    </tr>
    <tr>
        <td>/rule-test/run-full-aggregation</td>
        <td>POST</td>
        <td>Takes rules and events from an object and returns an aggregated object</td>
    </tr>
</table>

For these endpoints to be reachable the Eiffel Intelligence back-end 
[needs to be configured](https://github.com/eiffel-community/eiffel-intelligence/blob/master/src/main/resources/application.properties) 
with `test.aggregation.enabled: true`. The below command would result in a
json response of `{"status":true}` if this functionality is enabled.

    curl -X GET -H "Content-type: application/json" -H "Authorization: Bearer <azure-token> localhost:8080/rule-test

Example curl commands to these endpoints [can be found here](https://github.com/eiffel-community/eiffel-intelligence/blob/master/wiki/running-rules-on-objects.md)

## <a id="subscriptions" />/subscriptions

<table>
    <tr>
        <th>Endpoint</th>
        <th>Type</th>
        <th>Explanation</th>
    </tr>
    <tr>
        <td>/subscriptions</td>
        <td>GET</td>
        <td>
            Fetches all available subscriptions from the back-end.
            Selected subscriptions can be fetched by using the optional subscriptionNames parameter
            and a comma separated list e.g. subscriptionNames={name1},{name2}
        </td>
    </tr>
        <tr>
        <td>/subscriptions?subscriptionNames={name1},{name2}</td>
        <td>GET</td>
        <td>
            Fetches information about one or more subscriptions from the back-end. The parameter 
            subscriptionNames can take a comma separated list of names.
        </td>
    </tr>
    </tr>
        <tr>
        <td>/subscriptions/{name}</td>
        <td>GET</td>
        <td>Fetches information about a single subscription from the back-end</td>
    </tr>
    <tr>
        <td>/subscriptions</td>
        <td>POST</td>
        <td>Request to add one (or more) new subscription(s) included in JSON format</td>
    </tr>
    <tr>
        <td>/subscriptions</td>
        <td>PUT</td>
        <td>Request to update one (or more) subscription object(s) included in JSON format</td>
    </tr>
    <tr>
        <td>/subscriptions?subscriptionNames={name1}</td>
        <td>DELETE</td>
        <td>
            One (or more) subscription(s) can be deleted by using the required subscriptionNames parameter
            and a comma separated list e.g. subscriptionNames={name1},{name2}
        </td>
    </tr>
    <tr>
        <td>/subscriptions/{name}</td>
        <td>DELETE</td>
        <td>Deletes a single subscription from the back-end</td>
    </tr>
</table>

The `/subscriptions` endpoint can be called with `GET`, `POST`, `PUT` and `DELETE`.
More information, and examples, on the `/subscriptions` API can be found in the 
[Eiffel Intelligence back-end documentation](https://github.com/eiffel-community/eiffel-intelligence/tree/master/wiki/subscription-API.md).

A `POST` request with subscriptions in a file may look as the following example.

    curl -X POST -d @file_containing_list_of_json_objects -H "Content-Type: application/json" \
        -H "X-Auth-Token: <xauth-token>" -H "Authorization: Bearer <azure-token> \
        http://localhost:8080/subscriptions?backendurl="http://127.0.0.1:8090/"

Here is an example using this endpoint and the result it gives if template subscriptions exists.
The `backendurl` parameters is passed in to use a specified instance instead of the default back-end instance.

    curl -X GET -H "Authorization: Bearer <azure-token> http://localhost:8080/subscriptions?backendurl="http://127.0.0.1:8090/"

The front-end used is running on localhost and port 8080. EI front-end forwards the request to 127.0.0.1 
and port 8090 as requested in the query parameters and the result is a list of existing subscriptions:

    [
        {
            "aggregationtype":"eiffel-intelligence",
            "created":1543577879242,
            "notificationMeta":"http://eiffel-jenkins1:8080/job/ei-artifact-triggered-job/build",
            "notificationType":"REST_POST",
            "restPostBodyMediaType":"application/x-www-form-urlencoded",
            "notificationMessageKeyValues":[
                {
                    "formkey":"json",
                    "formvalue":"{parameter: [{ name: 'jsonparams', value : to_string(@) }]}"
                }
            ],
            "repeat":false,
            "requirements":[
                {
                    "conditions":[
                        {
                            "jmespath": "split(identity, '/') | [1] =='com.mycompany.myproduct'"
                        }
                    ]
                }
            ],
            "subscriptionName":"Subscription1",
            "userName":"functionalUser",
            "password":"",
            "ldapUserName":"",
            "_id":{
                "$oid":"5c012117aeb9d61aed160a2c"
            },
            "authenticationType":"BASIC_AUTH"
        },
        {
            "aggregationtype":"eiffel-intelligence",
            "created":1543577879265,
            "notificationMeta":"http://eiffel-jenkins2:8080/job/ei-artifact-triggered-job/build",
            "notificationType":"REST_POST",
            "restPostBodyMediaType":"application/x-www-form-urlencoded",
            "notificationMessageKeyValues":[
                {
                    "formkey":"json",
                    "formvalue":"{parameter: [{ name: 'jsonparams', value : to_string(@) }]}"
                }
            ],
            "repeat":false,
            "requirements":[
                {
                    "conditions":[
                        {
                            "jmespath": "split(identity, '/') | [1] =='com.mycompany.myproduct'"
                        }
                    ]
                }
            ],
            "subscriptionName":"Subscription2",
            "password":"",
            "ldapUserName":"",
            "_id":{
                "$oid":"5c012117aeb9d61aed160a2d"
            },
            "authenticationType":"NO_AUTH"
        },
        {
            "aggregationtype":"eiffel-intelligence",
            "created":1543577879280,
            "notificationMeta":"mymail@company.com",
            "notificationType":"MAIL",
            "restPostBodyMediaType":"",
            "notificationMessageKeyValues":[
                {
                    "formkey":"",
                    "formvalue":"{mydata: [{ fullaggregation : to_string(@) }]}"
                }
            ],
            "repeat":false,
            "requirements":[
                {
                    "conditions":[
                        {
                            "jmespath": "split(identity, '/') | [1] =='com.mycompany.myproduct'"
                        }
                    ]
                }
            ],
            "subscriptionName":"Subscription3_Mail_Notification",
            "password":"",
            "ldapUserName":"",
            "_id":{
                "$oid":"5c012117aeb9d61aed160a2e"
            },
            "authenticationType":"NO_AUTH"
        }
    ]
