# Curl Examples to front-end

## Introduction

The front-end has a bridge functionality that is built in as a part of the front-end's web server and handles requests coming from the web GUI towards any of the back-ends configured.
The `/backend` endpoint is the only additional endpoint which does not exist in [Eiffel Intelligence backend](https://github.com/eiffel-community/eiffel-intelligence).
The front-end may also be used by other tools such as CURL or any kind of program that can make HTTP(S) requests.
Below are some examples of using CURL towards different endpoints, together with example responses.
Most endpoints are also documented in the [Eiffel Intelligence backend repository](https://github.com/eiffel-community/eiffel-intelligence/tree/master/wiki/markdown)

#### Quick access to endpoints:
* [/auth](#auth)
* [/backend](#backend)
* [/download](#download)
* [/information](#information)
* [/query](#query)
* [/rules](#rules)
* [/subscriptions](#subscriptions)


## <a id="auth" /> /auth

<table>
    <tr>
        <th>Context Path</th>
        <th>Type</th>
        <th>Explanation</th>
    </tr>
    <tr>
        <td>/auth</td>
        <td>GET</td>
        <td>Check if LDAP security is enabled</td>
    </tr>
    <tr>
        <td>/auth/checkStatus</td>
        <td>GET</td>
        <td>Used to check the back-end status</td>
    </tr>
    <tr>
        <td>/auth/login</td>
        <td>GET</td>
        <td>Login to EI with a username and password</td>
    </tr>
    <tr>
        <td>/auth/logout</td>
        <td>GET</td>
        <td>Logout from EI</td>
    </tr>
</table>

A curl call with the command:

    curl -X GET http://localhost:8080/auth

Gives the response data `{"security":false}`. The `security` parameter would be true if LDAP was enabled.

It is also possible to login and logout using curl. The below command provides the `-u` flag followed by a username.

    curl -X GET -H "Content-type: application/json" -u <user> localhost:8080/auth/login

It is possible to provide both username and password directly in the request, as seen below.

    curl -X GET -H "Content-type: application/json" -u <user>:<password> localhost:8080/auth/login

More information and examples can be found in the [EI back-end documentation](https://github.com/eiffel-community/eiffel-intelligence/blob/master/wiki/markdown/Authentication.md)


## <a id="backend" /> /backend

<table>
    <tr>
        <th>Context Path</th>
        <th>Type</th>
        <th>Explanation</th>
    </tr>
    <tr>
        <td>/backend</td>
        <td>GET</td>
        <td>Retrieves a json list of available back-end instances</td>
    </tr>
    <tr>
        <td>/backend</td>
        <td>POST</td>
        <td>Adds one back-end instance</td>
    </tr>
    <tr>
        <td>/backend</td>
        <td>DELETE</td>
        <td>Deletes a given backend instance</td>
    </tr>
</table>

While the web GUI may use the back-end instances list and select different back-ends in an easy way, a user that uses the front-end without the included web GUI may need to specify a back-end URL. This may be done by adding a `backendurl` parameter to the request.

Note that for users where the front-end and back-end is running with the help of Tomcat there will be context paths used.

#### Default back-end

The front-end can be configured to use a default back-end, this means that if no back-end is specified when making a HTTP request to the front-end the default back-end will be used.
If you want to see a list of back-ends and see if there is a default back-end set, you may use the command:

###### Example GET request using curl:

    curl -X GET http://*front-end-url*/*context-path-if-any*/backend

###### Example GET request using curl, with host, port using Tomcat:

    curl -X GET http://localhost:8080/eifrontend/backend

###### Example GET request using curl, with EI default settings:

    curl -X GET http://localhost:8080/backend

The default back-end should have the key `defaultBackend` set to `true`. If the JSON list ends up empty there are no back-ends specified in the front-end. If there is no JSON object with the key set to true there is no default back-end.

In the response list all objects have a key `active` set to `true` or `false`. This key reveals which back-end instance has been selected in the GUI. A curl commands shows the default back-end as the `active` one. If no default back-end exists, the active should usually be the first object in the list.

If the back-end list lacks a default back-end one may be added by using a HTTP POST request. The injected object should be specified in JSON and look like the example below.

    [
        {"name":"Any default name", "host":"*Your back-end url*", "port":8090, "contextPath":"", "https":false, "defaultBackend":true}
    ]

You may add a back-end using for example CURL:

    curl -d '{"name":"My Default Back-End","host":"localhost","port":8090,"contextPath":"","https":false,"defaultBackend":true}' \
        -H "Content-Type: application/json" -X POST http://localhost:8080/*front-end-context-path*/backend

__Note:__ `name` must be unique.

Only one back-end instance can be added at a time.
Even with different names all elements must be unique, you may not have two or more elements with the same `host`, `port`, `contextPath` or `https` value, one of these three keys must be different. Only the `contextPath` key may be left empty.

###### Example of __valid__ back-end list:

    [
        {"name":"My Back-End","host":"localhost","port":8090,"contextPath":"","https":false,"defaultBackend":true},
        {"name":"My Back-End 2","host":"local-toast","port":8090,"contextPath":"","https":false,"defaultBackend":false},
        {"name":"My Back-End 3","host":"localhost","port":8091,"contextPath":"","https":false,"defaultBackend":false},
        {"name":"My Back-End 4","host":"localhost","port":8090,"contextPath":"/back-end","https":false,"defaultBackend":false},
        {"name":"My Back-End 5","host":"localhost","port":8090,"contextPath":"","https":true,"defaultBackend":false}
    ]

All entries has different `names`.
Second entry has different `host`, third entry has different `port`, third entry has different `contextPath`, fifth entry has `https` changed to true.

###### Example of __invalid__ back-end list:

    [
        {"name":"My Back-End","host":"localhost","port":8090,"contextPath":"","https":false,"defaultBackend":true},
        {"name":"My Back-End","host":"local-toast","port":12345,"contextPath":"/my-path","https":true,"defaultBackend":false},
        {"name":"My Back-End 2","host":"localhost","port":8090,"contextPath":"","https":false,"defaultBackend":false}
    ]

The second entry is invalid due to having the same name as the first entry. The third entry is invalid due to having the same value in all fields as the first entry.

#### Deleting a back-end instance via curl

It is possible to delete a back-end instance using curl. The full JSON object has to be specified to identify which instance should be deleted. It is not possible to delete a default backend instance.

    {"name":"My Back-End","host":"localhost","port":8090,"contextPath":"","https":false,"defaultBackend":false},

In the below command the JSON object is put in a file and sent along with the request.

    curl -X DELETE --data @data.json localhost:8080/backend


#### Specified back-end
As a user of the front-end you may want to specify your own back-end URL if you do not want to use the default back-end.
This is possible to do by injecting the back-end URL as a query parameter.
The parameters key should be `backendurl` then enter the full HTTP URL you wish to use. This back-end instance does not have to be specified in the list of available instances.

An example of a way to add such parameter is below, note that the "?" indicates that parameters has been added to the front-end url.
`localhost:8080` is the front-end url, and we want to access the context path /auth but on URL `http://127.0.0.1:8090/` that is the back-end we wish to use.


    curl -X GET http://localhost:8080/auth?backendurl="http://127.0.0.1:8090/"

Example with Tomcat:

    curl -X GET http://localhost:8080/eifrontend/auth?backendurl="http://127.0.0.1:8090/eibackend/"

This way of entering the `backendurl` may be the easiest way. It works with GET, POST and PUT requests. Currently entering just a back-end name is not supported.

Note: It is not possible to add the `backendurl` parameter as a JSON parameter.


## <a id="download" />/download

<table>
    <tr>
        <th>Context Path</th>
        <th>Type</th>
        <th>Explanation</th>
    </tr>
    <tr>
        <td>/download/subscriptionsTemplate</td>
        <td>GET</td>
        <td>Downloads a template with several predefined subscriptions</td>
    </tr>
    <tr>
        <td>/download/rulesTemplate</td>
        <td>GET</td>
        <td>Downloads a template with predefined rules</td>
    </tr>
    <tr>
        <td>/download/eventsTemplate</td>
        <td>GET</td>
        <td>Downloads a template with predefined events</td>
    </tr>
</table>

The EI front-end supports these endpoints. More information can be found in the [EI back-end documentation](https://github.com/eiffel-community/eiffel-intelligence/blob/master/wiki/markdown/Download-Files.md)

#### Some curl examples

This command would return a list of rules.

    curl -X GET -H "Content-type: application/json" http://localhost:8080/download/rulesTemplate

This command returns a list of predefined template events.

    curl -X GET -H "Content-type: application/json" http://localhost:8080/download/eventsTemplate

This command returns a list of several subscriptions. It is also possible to specify a file in which to save the downloaded objects in to.

    curl -X GET -H "Content-type: application/json" localhost:8080/download/subscriptionsTemplate --output myFile.json


## <a id="information" />/information

<table>
    <tr>
        <th>Context Path</th>
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

    curl -X GET -H "Content-type: application/json" localhost:8080/information

## <a id="query" />/query

<table>
    <tr>
        <th>Context Path</th>
        <th>Type</th>
        <th>Explanation</th>
    </tr>
    <tr>
        <td>/queryAggregatedObject</td>
        <td>GET</td>
        <td>Download a subscription template json file</td>
    </tr>
    <tr>
        <td>/queryMissedNotifications</td>
        <td>GET</td>
        <td>Queries any missed notifications</td>
    </tr>
    <tr>
        <td>/query</td>
        <td>POST</td>
        <td>Free style query; user specified queries</td>
    </tr>
</table>

Example curl commands to these endpoints [can be found here](https://github.com/eiffel-community/eiffel-intelligence/blob/master/wiki/markdown/Query.md) and [here](https://github.com/eiffel-community/eiffel-intelligence/blob/master/wiki/markdown/Query-aggregated-objects.md)


## <a id="rules" />/rules

<table>
    <tr>
        <th>Context Path</th>
        <th>Type</th>
        <th>Explanation</th>
    </tr>
    <tr>
        <td>/rules/rule-check/testRulePageEnabled</td>
        <td>GET</td>
        <td>Check if TestRules is enabled in the back-end</td>
    </tr>
    <tr>
        <td>/rules/rule-check/aggregation</td>
        <td>POST</td>
        <td>Takes rules and events from an object and returns an aggregated object</td>
    </tr>
</table>

For these endpoints to be reachable the Eiffel Intelligence back-end [needs to be configured](https://github.com/eiffel-community/eiffel-intelligence/blob/master/src/main/resources/application.properties) with `testaggregated.enabled: true`.
The below command would result in a json response of `{"status":true}` if this functionality is enabled.

    curl -X GET -H "Content-type: application/json" localhost:8080/rules/rule-check/testRulePageEnabled


Example curl commands to these endpoints [can be found here](https://github.com/eiffel-community/eiffel-intelligence/blob/master/wiki/markdown/Running-rules-on-objects.md)

## <a id="subscriptions" />/subscriptions

<table>
    <tr>
        <th>Context Path</th>
        <th>Type</th>
        <th>Explanation</th>
    </tr>
    <tr>
        <td>/subscriptions</td>
        <td>GET</td>
        <td>Fetches all available subscriptions from the back-end</td>
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
        <td>/subscriptions/*subscription name*</td>
        <td>GET</td>
        <td>Fetches information about a single (or multiple) subscription(s) from the back-end</td>
    </tr>
    <tr>
        <td>/subscriptions/*subscription name*</td>
        <td>DELETE</td>
        <td>Deletes a single (or multiple) subscription(s) from the back-end</td>
    </tr>
</table>


The `/subscriptions` endpoint can be called with `GET`, `POST`, `PUT` and `DELETE`.
More information, and examples, on the `/subscriptions` API can be found [here](https://github.com/eiffel-community/eiffel-intelligence/tree/master/wiki/markdown/Subscription-API.md).

A `POST` request with subscriptions in a file may look as the following example.

    curl -X POST -d @file_containing_list_of_json_objects -H "Content-Type: application/json" \
        http://localhost:8080/subscriptions?backendurl="http://127.0.0.1:8090/"

Here is an example using this endpoint and the result it gives in case we have the templated subscriptions added.
The `backendurl` parameters is passed in to use a specified instance instead of the default back-end instance.

    curl -X GET http://localhost:8080/subscriptions?backendurl="http://127.0.0.1:8090/"

The back-end used is running on localhost and port 8080. EI front-end forwards the request to 127.0.0.1 and port 8090 as requested in the query parameters and the result is as follows:

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
                            "jmespath":"gav.groupId=='com.othercompany.library'"
                        }
                    ]
                }
            ],
            "subscriptionName":"Subscription1",
            "userName":"functionalUser",
            "password":"",
            "ldapUserName":"",
            "notificationMessageKeyValuesAuth":[],
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
                            "jmespath":"gav.groupId=='com.othercompany.library'"
                        }
                    ]
                }
            ],
            "subscriptionName":"Subscription2",
            "password":"",
            "ldapUserName":"",
            "notificationMessageKeyValuesAuth":[],
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
                            "jmespath":"gav.groupId=='com.othercompany.library'"
                        }
                    ]
                }
            ],
            "subscriptionName":"Subscription3_Mail_Notification",
            "password":"",
            "ldapUserName":"",
            "notificationMessageKeyValuesAuth":[],
            "_id":{
                "$oid":"5c012117aeb9d61aed160a2e"
            },
            "authenticationType":"NO_AUTH"
        }
    ]

