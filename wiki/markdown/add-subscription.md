# Add Subscription

This form is used to add subscription through front-end GUI. Here is a
description of the elements available on this form.

<kbd>
    <img style="border:1px solid black" src="images/subscription_add_part1.png"></img>
</kbd>

## Load Subscription Template
In this dropdown list, options are given to select a specific template for 
the subscription. Currently, the following options are listed: 
Jenkins Pipeline Parametrized Job Trigger, REST POST and Mail Trigger.

## SubscriptionName
A field to give a name to the current subscription. Only
letters, underscore and numbers are allowed in this field.

<kbd>
    <img style="border:1px solid black" src="images/subscription_add_part2.png"></img>
</kbd>

## NotificationType
When the subscription is fulfilled there are two types of notifications: 
REST POST and mail notification. If Notification type MAIL is selected an 
email subject may be entered, but it is optional. Eiffel Intelligence will 
use the configured email subject from Eiffel Intelligence back-end 
application.properties if nothing is specified in the subscription.

## RestPostMediaType
Options for selecting a specific form content type. This is only when the 
notification type REST POST is selected. The options available are: 
'application/json' and 'application/x-www-form-urlencoded'.

## NotificationMessage
It is used to send a message to a specific client. The format of the 
message depends on the options selected in the NotificationMessage
and RestPostMediaType. A button, “Add Key/Value pair”, in this field may be
used to add more parameters, if the selected RestPostBodyMediaType is 
'application/x-www-form-urlencoded'. If the RestPostBodyMediaType is 
'application/json' only one json object is allowed in the notificationMessage.

## NotificationMeta
Is the selected subscriber to notify when a subscription is fulfilled. It 
could be an email address such as `example@mail.com` or a url like 
`host.com/endpoint`. If you wish to trigger a Jenkins job when a 
subscription is fulfilled then the url for the job could be set as the notificationMeta.
Parameters such as a Jenkins job-token can be included in this field.
**Note**: The job-token should not be mistaken for the API token that is
used as a password for authentication against the entire jenkins instance.

<kbd>
    <img style="border:1px solid black" src="images/subscription_add_part3.png"></img>
</kbd>

## Authentication
A list to select authentication type. These credentials should be used by 
Eiffel Intelligence to perform the HTTP POST notification towards an external 
API.  
* NO_AUTH: _No Authentication used_
* BASIC_AUTH: _Username and password will be Base 64 encoded_
* BASIC_AUTH_JENKINS_CSRF: Jenkins CSRF Protection (crumb), _Username and password will
be Base 64 encoded. A crumb will be fetched automatically before request is made.
(Currently default in many Jenkins instances). **Note**: Will work even when CSRF
is disabled in Jenkins._

<kbd>
    <img style="border:1px solid black" src="images/subscription_add_part4.png"></img>
</kbd>

## Repeat
It is possible to enable repeat, e.g. whether same subscription
should be re-triggered for new additions to the aggregated object. If disabled,
the notification will be triggered only the first time when requirements are
fulfilled. It doesn't matter if you have multiple requirements, it will only be triggered once.

## Requirement and Conditions
It is used to add requirements and conditions, which need to be fulfilled.
A requirement should contain at least one condition (with a specific format). 
More than one conditions may be added under one requirement by using the 
“Add Condition” button. It should be noted that conditions under one 
requirement are connected by a logical “AND”. Thus all conditions under 
one requirement need to be satisfied before a subscription is fulfilled. 
More than one requirements may be added with the “Add Requirement” button.
Requirements are connected by a logical “OR”.

**_More information about how to write Requirement and Conditions can be found [here](https://github.com/eiffel-community/eiffel-intelligence/blob/master/wiki/markdown/subscriptions.md#writing-requirements-and-conditions)._**
