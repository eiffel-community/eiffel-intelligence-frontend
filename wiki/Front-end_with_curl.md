# Curl Examples to front-end

The front-end is accessible not only via the web GUI.

## Introduction

The front-end has a bridge functionality that is built in as a part of the front-ends web server and handles request coming from the web GUI towards any of the back-ends configured. The front-end may also be used by other tools such as CURL or any kind of program that can make HTTP(S) requests.

#### The front-end handles:
* POST, PUT and GET requests
* Default back-end
* Input back-end

While the web GUI may use the back-end instances list and select different back-ends in an easy way, a user that uses the front-end without the included web GUI may need to specify a back-end URL. This may be done by adding a `backendurl` parameter to the request.

Note that for users where the front-end and back-end is running with the help of Tomcat there will be context paths used.

### Default back-end

The front-end may have been setup with a default back-end, this means that if no back-end is specified when making a HTTP request to the front-end the default back-end will be used.
If you want to see a list of back-ends and see if there is a default back-end set you may use the command:

###### Normal curl command for get:

    curl -X GET http://*front-end-url*/*contect-path-if-any*/backend

###### Curl command for get with host, port using Tomcat:

    curl -X GET http://localhost:8080/eifrontend/backend

###### Curl command using EI default settings:

    curl -X GET http://localhost:8080/backend


The default back-end should have the key `defaultBackend` set to `true`. If the JSON list ends up empty there are no back-ends specified in the front-end. If there is no JSON object with the key set to true there is no default back-end.

You may see that in the list, all objects have a key `active` set to `true or false`, this key points to the back-end that acts as default, if no default back-end exist, the active should usually be the first object in the list.

If the back-end list lacks a default back-end one may be added by using a HTTP POST request. The injected object should be specified in JSON and look like the example below.

    [
        {"name":"Any default name", "host":"*Your back-end url*", "port":8090, "contextPath":"", "https":false, "defaultBackend":true}
    ]

You may add a back-end using for example CURL:

    curl -d '{"name":"My Default Back-End","host":"localhost","port":8090,"contextPath":"","https":false,"defaultBackend":true}' \
        -H "Content-Type: application/json" -X POST http://localhost:8080/*front-end-context-path*/backend

__Note:__ `name` must be unique.

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


### Specified back-end
As a user of the front-end you may want to specify your own back-end URL if you do not want to use the default back-end.
This is possible to do by injecting the back-end URL as a query parameter.
The parameters key should be `backendurl` then enter the full HTTP URL you wish to use.

An example of a way to add such parameter is below, note that the "?" indicates that parameters has been added to the front-end url.
`localhost:8080` is the front-end url, and we want to access the context path /auth but on URL `http://127.0.0.1:8090/` that is the back-end we wish to use.


    curl -X GET http://localhost:8080/auth?backendurl="http://127.0.0.1:8090/"

Example with Tomcat:

    curl -X GET http://localhost:8080/eifrontend/auth?backendurl="http://127.0.0.1:8090/eibackend/"

This way of entering the `backendurl` may be the easiest way. It works with GET, POST and PUT requests. Currently entering just a back-end name is not supported.

Note: It is not possible to add the `backendurl` parameter as a JSON parameter.


## Endpoints bridged from front-end to back-End
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
        <td>Used to check the back-end status.</td>
    </tr>
    <tr>
        <td>/subscriptions</td>
        <td>GET</td>
        <td>Fetches all available subscriptions from the back-end</td>
    </tr>
    <tr>
        <td>/subscriptions</td>
        <td>POST</td>
        <td>Request to add a new subscription included as a JSON object</td>
    </tr>
    <tr>
        <td>/subscriptions</td>
        <td>PUT</td>
        <td>Request to update a given subscription object included as a JSON object</td>
    </tr>
    <tr>
        <td>/subscriptions/*subscription name*</td>
        <td>GET</td>
        <td>Fetches information about a single subscription from the back-end</td>
    </tr>
    <tr>
        <td>/subscriptions/*subscription name*</td>
        <td>DELETE</td>
        <td>Deletes a single instance from the back-end</td>
    </tr>
    <tr>
        <td>/information</td>
        <td>GET</td>
        <td>Fetches all information from about the back-end and it´s components</td>
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
        <td>GET</td>
        <td>Free style query, here the user may specify their queries</td>
    </tr>
    <tr>
        <td>/query</td>
        <td>POST</td>
        <td>Free style query, here the user may specify their queries</td>
    </tr>
    <tr>
        <td>/rules/rule-check/testRulePageEnabled</td>
        <td>GET</td>
        <td>Returns true or false depending if test rules function is enabled in the back-end</td>
    </tr>
    <tr>
        <td>/rules/rule-check/aggregation</td>
        <td>POST</td>
        <td>Takes rules, events from an object and returns an aggregated object</td>
    </tr>
</table>

## Curl Examples
A curl call with the command: `curl -X GET http://localhost:8080/auth` Gives the response data `{"security":false}`. The `security` parameter would be true if LDAP was enabled.

The `/subscriptions` endpoint is one that may be used the most, we may call that endpoint using three methods `GET`, `POST` and `PUT`.

A `POST` request with subscriptions in a file may look as the following example.


    curl -X POST -d @file_containing_list_of_json_objects -H "Content-Type: application/json" \
        http://localhost:8080/subscriptions?backendurl="http://127.0.0.1:8090/"

Here is an example using this endpoint and the result it gives in case we have the templated subscriptions added.


    curl -X GET http://localhost:8080/subscriptions?backendurl="http://127.0.0.1:8090/"

The back-end used is running on localhost and port 8080, we redirect the request to 127.0.0.1 and port 8090 as requested in the query parameters and the result is as follows

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

