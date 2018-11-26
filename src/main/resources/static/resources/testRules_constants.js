// Short usage description of test rules
var test_rule_info = '1. Load buttons are for loading rules/events from external files.\n' +
                    '    It is possible to choose if you want to append or replace\n' +
                    '    aready written rules/events in the text areas.\n'+
                    '      * The file content should be formatted as JSON objects in\n' +
                    '         a JSON list, ex. [{Object1}, {Object2}].\n' +
                    '\n2. By clicking on "Get Template" buttons you will download rules\n' +
                    '    respective events template. Rule´s template contains 3 rules\n' +
                    '    and event´s template contains 3 events.\n'+
                    '\n3. Add buttons are for adding new rules/events. When you click\n' +
                    '    on one of them, a new text area will show up.\n'+
                    '      * There can be only one JSON object per text area. One\n' +
                    '         rule/event per text area.\n' +
                    '\n4. Download buttons are for downloading edited rules/events.\n'+
                    '      * This enables you to edit rules/events locally and\n' +
                    '         then upload them using above mentioned load rules/\n' +
                    '         events buttons.\n' +
                    '\n5. "Clear All" buttons remove all rules respective events. It\n' +
                    '    is possible to remove single rule/event by clicking on trash\n' +
                    '    can button next to specific text area.\n'+
                    '\n6. Clicking on "Find Aggregated Object" button will start\n' +
                    '    the aggregation process. If rules and events are correct,\n' +
                    '    a pop up window with the aggregated object will show up on\n' +
                    '    the screen.\n' +
                    '\nFor more information visit:';

// Default template for rules
var ruleTemplate = {
  "TemplateName" : "",
  "Type" : "",
  "TypeRule" : "",
  "IdRule" : "",
  "StartEvent" : "",
  "IdentifyRules" : "",
  "MatchIdRules" : {},
  "ExtractionRules" : "",
  "DownstreamIdentifyRules" : "",
  "DownstreamMergeRules" : "",
  "DownstreamExtractionRules" : "",
  "ArrayMergeOptions" : "",
  "HistoryIdentifyRules" : "",
  "HistoryExtractionRules" : "",
  "HistoryPathRules" : "",
  "ProcessRules" : null,
  "ProcessFunction" : null
};