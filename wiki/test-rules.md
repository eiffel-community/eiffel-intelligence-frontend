# Test Rules User Guide

Clicking on the Test Rules element in the navigator window opens an
interface in the work window and interacts with the `/rule-test/run-single-rule`
end point of Eiffel Intelligence. This interface can be used to test
rules on events.

## “Test Rules” Graphical User Interface ##

The graphical user interface for testing rules consists of two panes.
Left pane is intended to add the rules and right pane is intended for
adding the Eiffel events.

<img src="images/testrules_overview.png" />

### Add ###

Add buttons are for adding new rules/events. When you click on one of
them, a new text area will show up. Then you can write or paste rule/event
into it. There can be only one JSON object per text area. This means
that it is not allowed to have more than one rule/event per text area.

### Load From File ###

Load buttons are for loading rules/events from external files. The file
content should be formatted as JSON objects in a JSON list, ex.
`[{Object1}, {Object2}]`. After clicking on the button, it is possible
to choose between replacing and appending to already written rules/events.
A pop-up window with those two options shows up.

<img src="images/testrules_replace_append.png" />

After that, you need to choose from which file you want to load rules/events.

<img src="images/testrules_browse_file.png" />

When the file has been chosen, rules or events are loaded into text areas.
In this example events template was loaded and the result can be seen on
the image below.

<img src="images/testrules_events.png" />

### Download ###

Download buttons are for downloading edited rules/events. This enables to
edit rules/events locally and then upload them using above mentioned
“Load From File” buttons.

### Get Template ###

By clicking on "Get Template" buttons you will download rules respective
events template. Each template file contain several template objects.

[Rules template can be found here](https://github.com/eiffel-community/eiffel-intelligence/blob/master/src/main/resources/templates/rules.json).
[Eiffel events template can be found here](https://github.com/eiffel-community/eiffel-intelligence/blob/master/src/main/resources/templates/events.json).

### Clear All Rules/Clear All Events ###

"Clear All" buttons remove all rules respective events. After clicking on
button, a pop-up window shows up and asks for confirmation. After confirming
all rules or events are removed.

<img src="images/testrules_clear_all.png" />

### Trash Can Button ###

It is possible to remove a single rule/event by clicking on trash can
button next to specific text area.

### Find Aggregated Object ###

Clicking on "Find Aggregated Object" button will start the aggregation
process. If rules and events are correct, a pop-up window with the
aggregated object will show up on the screen.

<img src="images/testrules_aggregated_object.png" />

## Curl ##

It is possible to use curl to get required information. To get information
about test rules status, if this functionality is enabled in back-end or
not, you can execute command below.

    curl -X GET http://<host>:8080/rule-test?backendurl="http://127.0.0.1:8090/"

To execute rules on specific events with curl, you need to create a JSON
file with rules and events. File should contain:

    {
        "listEventsJson": [
            {Event1},
            {Event2}
            ...
        ],
        "listRulesJson": [
            {Rule1},
            {Rule2},
            ...
        ]
    }

And then run curl command below.

    curl -X POST -d "@<path to file>" -H "Content-Type: application/json" http://<host>:8080/rule-test/run-full-aggregation?backendurl="http://127.0.0.1:8090/"

## Write rules

**More information about how to write rules can be found [here](https://github.com/eiffel-community/eiffel-intelligence/blob/master/wiki/rules.md).**
