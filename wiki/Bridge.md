# Bridge

The Bridge is a part of the Eiffel Intelligence front end.

<h3>Introduktion</h3>
<p>
The Bridge is always used by the front-end web GUI as a connection between the back end instances and the GUI.
The bridge functionality is built in as a part to the front ends web server and handles request coming from the web GUI towards any of the back ends configured.
The bridge may also be used by other applications such as CURL or any kind of program that can make HTTP(S) requests.
</p>

<h3>The Bridge handels:</h3>
<ul>
   <li>POST, PUT and GET requests</li>
   <li>Default back end</li>
   <li>Input back end</li>
</ul>
<p>
While the web gui may use the back-end instances list and select different back ends in an easy way, a user that uses the bridge without the included web GUI may need to specify a back end URL.
This may be done by adding a backendurl parameter to the request.
</p>

<h3>Default back end</h3>
<p>
The bridge may have been setup mwith a default back end, this means that if no back end is specified when making a HTTP request to the bridge the default back end will be used.
If you want to see a list of back ends and see if there is a default back end set you may use the command:
</p>
<pre>
   curl -X GET http://*url to bridge*/backend
</pre>
<p>
The default back end should have the key <code>defaultBackend</code> set to <code>true</code>. If the JSON list ends up empty there is no back ends specified in the bridge. If there is no JSON object with the key set to true there is no default back end.
</p>
<p>
You may see that in the list you get, all objects have a key <code>active</code> set to <code>true or false</code>, this key points to the back end that acts as default, if no default back end exist, the active should usually be the first object in the list.
</p>
<p>
If the back-end list lack a default back end one may be added by using a HTTP POST request. The injected object should be specified in JSON and look like the example below.
</p>
<pre>
{"name":"Any default name", "host":"*Your back end url*", "port":8090, "contextPath":"", "https":false, "defaultBackend":true}
</pre>
<p>You may add a back end using for example CURL:</p>
<pre>
   curl -d '{"name":"My Default Back End", "host":"localhost", "port":8090, "contextPath":"", "https":false, "defaultBackend":true}' -H "Content-Type: application/json" -X POST http://localhost:8080/backend
</pre>
<p>Note: Back end names must be unique and only two elements may not have the same "host", "port" and "contextPath", one of these three keys must be different. There may also only be one default back end instance.</p>


<h3>Specified back end</h3>
<p>
As a user of the bridge you may want to specify your own back end URL if you do not want to use the default back end.
This is possible to do by injecting the back end URL as a query parameter.
The parameters key should be <code>backendurl</code> then enter the full HTTP URL you wish to use.
</p>
<p>
An example of a way to add such parameter is below, note that the "?" indicates that parameters has been added to the bridge url.
<code>Localhost:8080</code> is the bridge url, and we want to access the context path /auth but on URL <code>http://127.0.0.1:8090/</code> that is the back end we wish to use.
</p>
<pre>curl -X GET http://localhost:8080/auth?backendurl="http://127.0.0.1:8090/"</pre>
<p>
This way of entering the backendurl may be the easiest way. It works with GET, POST and PUT requests.
</p>
<p>
Note: It is not possible to add the backendurl parameter as a JSON parameter.
</p>

<h3>Endpoints Bridged To Back End</h3>
<table>
   <tr>
      <th>Context Path</th>
      <th>Type</th>
      <th>Explenation</th>
   </tr>
   <tr>
      <td>/auth</td>
      <td>GET</td>
      <td>Authentication status, is LDAP enabled</td>
   </tr>
   <tr>
      <td>/auth/checkStatus</td>
      <td>GET</td>
      <td>Back end status, is back end online</td>
   </tr>
   <tr>
      <td>/subscriptions</td>
      <td>GET</td>
      <td>Fetches all available subscriptions from the back end</td>
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
      <td>Fetches information about a single subscription from the back end</td>
   </tr>
   <tr>
      <td>/subscriptions/*subscription name*</td>
      <td>DELETE</td>
      <td>Deletes a single instance from the back end</td>
   </tr>
   <tr>
      <td>/information</td>
      <td>GET</td>
      <td>Fetches all information from about the back end and it´s components</td>
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
      <td>gives a result weather the test rules function is enabed in the back end</td>
   </tr>
   <tr>
      <td>/rules/rule-check/aggregation</td>
      <td>POST</td>
      <td>*Needs update*</td>
   </tr>
</table>
<p>Lets do some examples on Curl commands</p>

<h3>Curl Examples</h3>
<p>
A curl call with the command: <pre>curl -X GET http://localhost:8080/auth</pre> Gives the response data <code>{"security":false}</code> would be true if LDAP was enabled.
</p>
<p>
The <code>/subscriptions</code> endpoint is one that may be used the most, we may call that endpoint using three methods <code>GET, POST and PUT</code>.
</p>
<p>
A POST request with subscriptions in a file may look as the following example.
</p>
<pre>
   curl -X POST -d @file_containing_list_of_json_objects -H "Content-Type: application/json" http://localhost:8080/subscriptions?backendurl="http://127.0.0.1:8090/"
</pre>
<p>
Here is an example using this endpoint and the result it gives in case we have the templated subscriptions added.
</p>
<pre>
   curl -X GET http://localhost:8080/subscriptions?backendurl="http://127.0.0.1:8090/"
</pre>
<p>The back end used is running on localhost and port 8080, we redirect the request to 127.0.0.1 and port 8090 as requested in the query parameters and the result is as follows </p>
<pre>
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
        "notificationMessageKeyValuesAuth":[

        ],
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
        "notificationMessageKeyValuesAuth":[

        ],
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
        "notificationMessageKeyValuesAuth":[

        ],
        "_id":{
          "$oid":"5c012117aeb9d61aed160a2e"
        },
        "authenticationType":"NO_AUTH"
      }
    ]
</pre>
