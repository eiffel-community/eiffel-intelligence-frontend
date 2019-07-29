# Subscription Handling

Clicking on the Subscription Handling element will display some buttons in main
page which help perform a number of subscription related actions. A table may
become visible, which is populated by the information about the stored (if any)
subscriptions in the connected database. This view is also the default view of
the Eiffel Intelligence front-end, once the authentication (if enabled) is done.

<kbd>
    <img style="border:1px solid black" src="images/subscription_overview.png"></img>
</kbd>

#### _Add Subscription_
This button opens a form with a number of fields to create a subscription
through subscription endpoint (POST /subscriptions).
#### _Upload Subscriptions_
This button help uploads a subscription by opening a file explorer.
#### _Bulk Delete_
This button deletes all the selected subscriptions in the table from the
database.
#### _Bulk Download_
This button downloads all the selected subscriptions in the table.
#### _Reload_
This button reloads the data from the database and refresh the data in the
subscription table.
#### _Get Template_
This button downloads a subscription template.
#### _EI Back-end Status_
This button indicates, through its color, whether a back-end instance is
connected with front-end or not. The green color means back-end is connected
while red means no instance is connected.

## Single Subscription Handling

#### _Checkbox_
Used to select one or many subscription for bulk usages. The top one is a
select all checkbox.
#### _SubscriptionName_
The subsctiption name.
#### _Date_
The date when the subscription was created and saved in Eiffel Intelligence.
#### _NotificationType_
What kind of notification type the subscription uses.
#### _NotificationMeta_
The URL the subscription triggers or email adresses that will be notified on
a subscription trigger.
#### _Repeat_
True or False depending weather or not repeat is activated.
#### _Action_
A set of buttons handling different kind of actions for that subscription.
When hovering with a pointer over the button a descriptor what the button
does will be displayed.

<kbd>
    <img style="border:1px solid black" src="images/subscription_buttons.png"></img>
</kbd>

##### _View_
The view button is represented as an eye symbol and will open a modal to view data in
the subscription.
##### _Clone_
The clone button is represented as an copy symbol and will open a modal to add a new
subscription containing the same information as the cloned subscription except
the name that must be unique for all subscriptions.
##### _Download_
The download button is represented as an download symbol and when pressed will open a
download window where the user may save the subscription data in .json format.
##### _Edit_
The edit button is represented as an pen symbol and will open a modal to edit data in
the subscription.
##### _Delete_
The delete button is represented as an trashcan symbol and will open confirmation dialogue
to delete the subscription.

**_More Subscriptions related information can be found [here](https://github.com/eiffel-community/eiffel-intelligence/tree/master/wiki/markdown/subscription-API.md) and [here](https://github.com/eiffel-community/eiffel-intelligence/tree/master/wiki/markdown/subscriptions.md)_**
