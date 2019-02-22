// Global vars
var i = 0;
var isReplacing = true;

jQuery(document).ready(function () {
    // Model for knockout(KO) binding
    function AppViewModel() {
        var self = this;
        self.rulesBindingList = ko.observableArray([]);
        self.eventsBindingList = ko.observableArray([]);
        self.parsedToString = function (item) {
            return JSON.stringify(item, null, 2);
        };
        // Removing the rule
        self.removeRule = function (data, event) {
            var context = ko.contextFor(event.target);
            self.rulesBindingList.splice(context.$index(), 1);
            if (self.rulesBindingList().length == 0) {
                logMessages("Deleted all rule types, but we need atleast one Rule type, Here add default rule type");
                self.addRule(ruleTemplate);
            }
        };

        // Removing the events
        self.removeEvent = function (data, event) {
            var context = ko.contextFor(event.target);
            self.eventsBindingList.splice(context.$index(), 1);
            if (self.eventsBindingList().length == 0) {
                logMessages("Deleted all events, but we need atleast one event.");
                self.addEvent({});
            }
        };

        self.validateJSON = function (observableArray) {
            var array = [];
            ko.utils.arrayForEach(observableArray, function (element) {
                try {
                    array.push(JSON.parse(element.data()));
                } catch (e) {
                    logMessages("Invalid json rule format :\n" + element.data());
                    return false;
                }
            });
            return array;
        };

        //This submit function for finding the aggregated object from the rules and events, This function internally call the ajax call
        self.submit = function () {
            var rules = self.validateJSON(self.rulesBindingList());
            var events = self.validateJSON(self.eventsBindingList());
            var rulesAndEventsJson = { 'listRulesJson': rules, 'listEventsJson': events };

            var callback = {
                success: function (responseData, textStatus) {
                    if (responseData.length > 0) {
                        $('#aggregatedObjectContent').text(JSON.stringify(responseData, null, 2));
                        $('#aggregatedObjectModal').modal('show');
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    if (XMLHttpRequest.responseText == "") {
                        logMessages("Failed to generate the aggregated object, Error: Could not contact the backend server.");
                    } else {
                        logMessages("Failed to generate the aggregated object, Error: " + XMLHttpRequest.responseText);
                    }
                }
            };

            var ajaxHttpSender = new AjaxHttpSender();
            var contextPath = "/rules/rule-check/aggregation";
            ajaxHttpSender.sendAjax(contextPath, "POST", JSON.stringify(rulesAndEventsJson), callback);
        };

        // This function for adding rule
        self.addRule = function (data) {
            self.rulesBindingList.push({ 'data': ko.observable(self.parsedToString(data)) });
        };
        // This function for adding rule
        self.addEvent = function (data) {
            self.eventsBindingList.push({ 'data': ko.observable(self.parsedToString(data)) });
        };

        // This function is used to remove all rules
        self.clearAllRules = function () {
            self.rulesBindingList([]);
            self.addRule(ruleTemplate);
            $('.delete-warning-modal').modal("hide");
        }

        // This function is used to remove all events
        self.clearAllEvents = function () {
            self.eventsBindingList([]);
            self.addEvent({});
            $('.delete-warning-modal').modal("hide");
        }

        // This function is used to remove all rules
        self.clearType = ko.observable("rules");
        self.confirmClearAll = function (clearType) {
            self.clearType(clearType);
            $('.delete-warning-modal').modal('show');
        }

        return self;
    }

    //Create information modal
    $(".rules_info").click(function (event) {
        event.stopPropagation();
        event.preventDefault();
        $('#infoContent').text(test_rule_info);
        $('#infoModal').modal('show');
    });

    var vm = new AppViewModel();
    ko.applyBindings(vm, $("#submitButton")[0]);
    ko.applyBindings(vm, $("#testRulesDOMObject")[0]);
    ko.applyBindings(vm, $("#testEventsDOMObject")[0]);
    ko.applyBindings(vm, $(".delete-warning-modal")[0]);
    vm.addRule(ruleTemplate);
    vm.addEvent({});

    function validateJSONAndUpload(subscriptionFile, isRules) {
        var reader = new FileReader();
        reader.onload = function () {
            var fileContent = reader.result;
            var jsonLintResult = "";
            try {
                jsonLintResult = jsonlint.parse(fileContent);
            } catch (e) {
                logMessages("JSON events Format Check Failed:\n" + e.name + "\n" + e.message);
                return false;
            }
            $.jGrowl('JSON events Format Check Succeeded', {
                sticky: false,
                theme: 'Notify'
            });

            var list = JSON.parse(fileContent);
            if (isRules) {
                if (isReplacing) {
                    vm.rulesBindingList([]);
                }
                list.forEach(function (element) {
                    vm.addRule(element);
                });
            } else {
                if (isReplacing) {
                    vm.eventsBindingList([]);
                }
                list.forEach(function (element) {
                    vm.addEvent(element);
                });
            }
        };

        if (subscriptionFile != null) {
            reader.readAsText(subscriptionFile);
        }
    }

    //Set onchange event on the input element "uploadRulesFile" and "uploadEventsFile"
    var pomRules = document.getElementById('uploadRulesFile');
    pomRules.onchange = function uploadFinished() {
        var subscriptionFile = pomRules.files[0];
        validateJSONAndUpload(subscriptionFile, true);
        $(this).val("");
    };

    var pomEvents = document.getElementById('uploadEventsFile');
    pomEvents.onchange = function uploadFinished() {
        var subscriptionFile = pomEvents.files[0];
        validateJSONAndUpload(subscriptionFile, false);
        $(this).val("");
    };

    //Upload events list json data
    $(".upload_rules").click(function (event) {
        event.stopPropagation();
        event.preventDefault();
        var isRules = true;
        replaceAppendModal(isRules);

    });

    //Upload list of events json data
    $(".upload_events").click(function (event) {
        event.stopPropagation();
        event.preventDefault();
        var isRules = false;
        replaceAppendModal(isRules);
    });

    function replaceAppendModal(isRules) {
        $('#AppendReplaceModal').modal('show');

        document.getElementById('replaceButton').onclick = function () {
            $('#AppendReplaceModal').modal('hide');
            isReplacing = true;
            if (isRules) {
                createRulesUploadWindow();
            } else {
                createUploadWindow();
            }
        };

        document.getElementById('appendButton').onclick = function () {
            $('#AppendReplaceModal').modal('hide');
            isReplacing = false;
            if (isRules) {
                createRulesUploadWindow();
            } else {
                createUploadWindow();
            }
        };
    }

    function createRulesUploadWindow() {
        if (document.createEvent) {
            var event = document.createEvent('MouseEvents');
            event.initEvent('click', true, true);
            pomRules.dispatchEvent(event);
        } else {
            pomRules.click();
        }
    }

    function createUploadWindow() {
        if (document.createEvent) {
            var event = document.createEvent('MouseEvents');
            event.initEvent('click', true, true);
            pomEvents.dispatchEvent(event);
        } else {
            pomEvents.click();
        }
    }

    // Download the modified rule
    $(".download_rules").click(function () {
        var formRules = [];
        $('.formRules').each(function () {
            try {
                formRules.push(JSON.parse($(this).val()));
            } catch (e) {
                logMessages("Invalid json format :\n" + $(this).val());
                return false;
            }
        });
        if (formRules.length !== 0) {
            var jsonData = JSON.stringify(formRules, null, 2);
            downloadFile(jsonData, "application/json;charset=utf-8", "rules.json");
        } else {
            logMessages("Data not available for download!");
        }
    });

    // Download the modified events
    $(".download_events").click(function () {
        var formEvents = [];
        $('.formEvents').each(function () {
            try {
                formEvents.push(JSON.parse($(this).val()));
            } catch (e) {
                logMessages("Invalid json format :\n" + $(this).val());
                return false;
            }
        });
        if (formEvents.length !== 0) {
            var jsonData = JSON.stringify(formEvents, null, 2);
            downloadFile(jsonData, "application/json;charset=utf-8", "events.json");
        } else {
            logMessages("Data not available for download!");
        }
    });

    function getTemplate(name) {
        var callback = {
            success: function (responseData, textStatus) {
                var jsonString = JSON.stringify(responseData, null, 2);
                downloadFile(jsonString, "application/json;charset=utf-8", name + ".json");
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                logMessages("Failed to download template, Error: Could not contact the backend server.");
            }
        };

        var ajaxHttpSender = new AjaxHttpSender();
        var contextPath = "/download/" + name;
        ajaxHttpSender.sendAjax(contextPath, "GET", "", callback);
    }

    // Download the rules template
    $(".download_rules_template").click(function (event) {
        event.stopPropagation();
        event.preventDefault();
        getTemplate("rulesTemplate");
    });

    // Download the events template
    $(".download_events_template").click(function (event) {
        event.stopPropagation();
        event.preventDefault();
        getTemplate("eventsTemplate");
    });

    // Start to check is backend Test Rule service status
    function checkTestRulePageEnabled() {
        var callback = {
            error: function () {
                addStatusIndicator(statusType.danger, statusText.backend_down);
                elementsDisabled(true);
            },
            success: function (responseData) {
                var isEnabled = responseData.status;
                if (isEnabled != true) {
                    addStatusIndicator(statusType.warning, statusText.test_rules_disabled);
                } else {
                    removeStatusIndicator();
                }
                elementsDisabled(!isEnabled);
            }
        };
        var ajaxHttpSender = new AjaxHttpSender();
        var contextPath = "/rules/rule-check/testRulePageEnabled";
        ajaxHttpSender.sendAjax(contextPath, "GET", null, callback);
    }
    // Finish to check backend Test Rule Service status

    function elementsDisabled(disabled) {
        $('.main button.btn').prop("disabled", disabled);
        $('textarea').prop("disabled", disabled);
    }

    // Check EI Backend Server Status ########################################
    function checkBackendStatus() {
        var callback = {
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                if (XMLHttpRequest.status == 401) {
                    doIfUserLoggedOut();
                } else {
                    doIfSecurityOff();
                }
            },
            success: function (responseData, textStatus) {
                checkBackendSecured();
            },
            complete: function () {
                checkTestRulePageEnabled();
            }
        };
        var ajaxHttpSender = new AjaxHttpSender();
        var contextPath = "/auth/checkStatus";
        ajaxHttpSender.sendAjax(contextPath, "GET", null, callback);
    }
    checkBackendStatus();

    // Check if EI Backend Server is online every X seconds
    if (timerInterval != null) {
        window.clearInterval(timerInterval);
    }
    timerInterval = window.setInterval(function () { checkBackendStatus(); }, 15000);

    // END OF EI Backend Server check #########################################
});
