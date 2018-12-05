# Add Subscription

This form is used to add subscription through frontend GUI. Here is a 
description of the elements available on this form.

**Load Subscription Template:** In this dropdown list options are given to 
select a specific template for the subscription form. Currently, the following 
options are listed: Jenkins Pipeline Parametrized Job Trigger, REST POST and 
Mail Trigger.

**SubscriptionName:** A field to give a name to the current subscription. Only 
letters, underscore and numbers are allowed in this field.

**NotificationType:** A dropdown list with two options, i.e., REST POST and 
Mail Trigger, depending on how a subscriber want to be notified when a 
subscription is fulfilled.

**RestPostMediaType:** A dropdown list that provides options for selecting a 
specific form content type. The options available in the list depends on the 
selected template type.

**NotificationMessage:** It is used to send a message to a specific client. The 
format of the message depends on the options selected in the NotificationMessage 
and RestPostMediaType. A button, “Add Key/Value pair”, in this field may be 
used to add more messages.

**Authorization:** A list to select authorization type. Currently, only one 
authorization type is provided which is “BASIC_AUTH”. The option “NO_AUTH” 
implies that authorization is not required.

**Repeat:** A drop down list to choose either true or false. It depends whether 
same subscription should be re-triggered for new additions to the aggregated 
object. If false, the notification will be triggered only the first time when 
conditions are fulfilled. 

**NotificationMeta:** A unique address need to be added for notification.

**Requirement:** It is used to add a requirement, which need to be fulfilled 
before a subscription is triggered. Requirement is added in the form of a 
condition (with a specific format). More than one conditions may be added under 
one requirement by using “Add Condition” button. It should be noted that 
conditions under one requirement are connected by logical “AND”. Thus all 
conditions udder one requirement need to be satisfied before a subscription is 
triggered. More than one requirements may be added by “Add Requirement” button. 
Requirements are connected by logical “OR”.
