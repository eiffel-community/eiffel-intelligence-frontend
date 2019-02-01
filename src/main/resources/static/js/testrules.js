// Global vars
var frontendServiceUrl;
var i = 0;
var isReplacing = true;

jQuery(document).ready(function () {
  frontendServiceUrl = $('#frontendServiceUrl').text();
  loadTooltip();

  // /Start ## Global AJAX Sender function ##################################
  var AjaxHttpSender = function () {
  };

  AjaxHttpSender.prototype.sendAjax = function (url, type, data, callback) {
    $.ajax({
      url: url,
      type: type,
      data: data,
      contentType: 'application/json; charset=utf-8',
      dataType: "json",
      cache: false,
      beforeSend: function () {
        callback.beforeSend();
      },
      error: function (XMLHttpRequest, textStatus, errorThrown) {
        callback.error(XMLHttpRequest, textStatus, errorThrown);
      },
      success: function (data, textStatus) {
        callback.success(data, textStatus);
      },
      complete: function (XMLHttpRequest, textStatus) {
        callback.complete();
      }
    });
  }
  // /Stop ## Global AJAX Sender function ##################################

  //Function for validating the json format, it accepts only string json
  function isValidJSON(str) {
    if (typeof (str) !== 'string') {
      return false;
    }
    try {
      JSON.parse(str);
      return true;
    } catch (e) {
      window.logMessages(XMLHttpRequest.responseText);
      return false;
    }
  }

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
        window.logMessages("Deleted all rule types, but we need atleast one Rule type, Here add default rule type");
        self.addRule(ruleTemplate);
      }
    };

    // Removing the events
    self.removeEvent = function (data, event) {
      var context = ko.contextFor(event.target);
      self.eventsBindingList.splice(context.$index(), 1);
      if (self.eventsBindingList().length == 0) {
        window.logMessages("Deleted all events, but we need atleast one event.");
        self.addEvent({});
      }
    };

    self.validateJSON = function (observableArray) {
      var array = [];
      ko.utils.arrayForEach(observableArray, function (element) {
        try {
          array.push(JSON.parse(element.data()));
        } catch (e) {
          window.logMessages("Invalid json rule format :\n" + element.data());
          return false;
        }
      });
      return array;
    };

    //This submit function for finding the aggregated object from the rules and events, This function internally call the ajax call
    self.submit = function () {
      var rules = self.validateJSON(self.rulesBindingList());
      var events = self.validateJSON(self.eventsBindingList());

      var callback = {
        beforeSend: function () {
        },
        success: function (data, textStatus) {
          if (data.length > 0) {
            $.jGrowl("Successfully aggregated object generated", {
              sticky: false,
              theme: 'Error'
            });

            $('#aggregatedObjectContent').text(JSON.stringify(data, null, 2));
            $('#aggregatedObjectModal').modal('show');
          }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
          if (XMLHttpRequest.responseText == "") {
            window.logMessages("Failed to generate the aggregated object, Error: Could not contact the backend server.");
          } else {
            window.logMessages("Failed to generate the aggregated object, Error: " + XMLHttpRequest.responseText);
          }
        },
        complete: function () {
        }
      };

      var ajaxHttpSender = new AjaxHttpSender();
      ajaxHttpSender.sendAjax(frontendServiceUrl + "/rules/rule-check/aggregation", "POST", JSON.stringify(JSON.parse('{"listRulesJson":'
        + JSON.stringify(rules) + ',"listEventsJson":' + JSON.stringify(events) + '}')), callback);
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
  $(".container").on("click", "button.rules_info", function (event) {
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
        window.logMessages("JSON events Format Check Failed:\n" + e.name + "\n" + e.message);
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
  $(".container").on("click", "button.upload_rules", function (event) {
    event.stopPropagation();
    event.preventDefault();
    var isRules = true;
    replaceAppendModal(isRules);

  });

  //Upload list of events json data
  $(".container").on("click", "button.upload_events", function (event) {
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
  $('.container').on('click', 'button.download_rules', function () {
    var formRules = [];
    $('.formRules').each(function () {
      try {
        formRules.push(JSON.parse($(this).val()));
      } catch (e) {
        window.logMessages("Invalid json format :\n" + $(this).val());
        return false;
      }
    });
    if (formRules.length !== 0) {
      var jsonData = JSON.stringify(formRules, null, 2);
      downloadFile(jsonData, "application/json;charset=utf-8", "rules.json");
    } else {
      window.logMessages("Data not available for download!");
    }
  });

  // Download the modified events
  $('.container').on('click', 'button.download_events', function () {
    var formEvents = [];
    $('.formEvents').each(function () {
      try {
        formEvents.push(JSON.parse($(this).val()));
      } catch (e) {
        window.logMessages("Invalid json format :\n" + $(this).val());
        return false;
      }
    });
    if (formEvents.length !== 0) {
      var jsonData = JSON.stringify(formEvents, null, 2);
      downloadFile(jsonData, "application/json;charset=utf-8", "events.json");
    } else {
      window.logMessages("Data not available for download!");
    }
  });

  function getTemplate(name) {
    var request = new XMLHttpRequest();
    request.open("GET", frontendServiceUrl + '/download/' + name, true);
    request.responseType = "application/json;charset=utf-8";
    request.onload = function (event) {
      if (this.responseText == "") {
        window.logMessages("Failed to download template, Error: Could not contact the backend server.");
      } else {
        var jsonData = JSON.stringify(JSON.parse(request.response), null, 2);
        downloadFile(jsonData, "application/json;charset=utf-8", name + ".json");
      }
    };
    request.send();
  }

  // Download the rules template
  $('.container').on('click', 'button.download_rules_template', function (event) {
    event.stopPropagation();
    event.preventDefault();
    getTemplate("rulesTemplate");
  });

  // Download the events template
  $('.container').on('click', 'button.download_events_template', function (event) {
    event.stopPropagation();
    event.preventDefault();
    getTemplate("eventsTemplate");
  });

  // Start to check is backend Test Rule service status
  var isEnabled = true;
  $.ajax({
    url: frontendServiceUrl + "/rules/rule-check/testRulePageEnabled",
    contentType: 'application/json; charset=utf-8',
    type: 'GET',
    error: function () { },
    success: function (data) {
      isEnabled = JSON.parse(ko.toJSON(data)).status;
      if (isEnabled != true) {
        displayOverlay("Test Rule service is not enabled! To enable it set the backend property [testaggregated.enabled] as [true]")
      }
    },
    complete: function () { }
  });
  // Finish to check backend Test Rule Service status

  function displayOverlay(text) {
    var overlay = document.createElement('div');
    overlay.setAttribute('id', 'overlay')
    overlay.setAttribute('class', 'testRulePage d-flex')
    var padding = document.createElement('div');
    padding.setAttribute('class', 'col-lg-3 col-md-1 d-none d-lg-block');
    var main = document.createElement('div');
    main.setAttribute('class', 'col-lg-9 col-md-11 col-12 flexbox');
    var textNode = document.createTextNode(text);
    main.appendChild(textNode);
    overlay.appendChild(padding);
    overlay.appendChild(main);
    var parent = document.getElementById('testRule');
    parent.appendChild(overlay);
  }

  function loadTooltip() {
    $('[data-toggle="tooltip"]').tooltip({ trigger: "click", html: true });
  }

  function closeTooltip() {
    $('.tooltip').tooltip('hide');
  }
});
