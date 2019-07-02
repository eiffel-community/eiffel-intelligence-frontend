# Subscription Handling

Clicking on the Subscription Handling element will display some buttons in main
page which help perform a number of subscription related actions. A table may
become visible, which is populated by the information about the stored (if any)
subscriptions in the connected database. This view is also the default view of
the Eiffel Intelligence front-end, once the authentication (if enabled) is done.

<img src="images/subscription_overview.png">
</img>

#### _Add Subscription_
This button opens a form with a number of fields to create a subscription
through subscription endpoint (POST /subscriptions).
#### _Upload Subscriptions_
This button help uploads a subscription by opening a file explorer
#### _Bulk Delete_
This button deletes all the selected subscriptions in the table from the
database.
#### _Reload_
This button reloads the data from the database and refresh the data in the
subscription table
#### _Get Template_
This button downloads a subscription template
#### _EI Back-end Status_
This button indicates, through its color, whether a back-end instance is
connected with front-end or not. The green color means back-end is connected
while red means no instance is connected.


**_More Subscriptions related information can be found [here](https://github.com/eiffel-community/eiffel-intelligence/tree/master/wiki/markdown/subscription-API.md) and [here](https://github.com/eiffel-community/eiffel-intelligence/tree/master/wiki/markdown/subscriptions.md)_**
