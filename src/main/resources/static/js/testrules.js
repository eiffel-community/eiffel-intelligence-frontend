// Global vars
var frontendServiceUrl;
var i = 0;
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
jQuery(document).ready(
    function() {

      frontendServiceUrl = $('#frontendServiceUrl').text();

      // /Start ## Global AJAX Sender function ##################################
      var AjaxHttpSender = function() {
      };

      AjaxHttpSender.prototype.sendAjax = function(url, type, data, callback) {
        $.ajax({
          url : url,
          type : type,
          data : data,
          contentType : 'application/json; charset=utf-8',
          dataType : "json",
          cache : false,
          beforeSend : function() {
            callback.beforeSend();
          },
          error : function(XMLHttpRequest, textStatus, errorThrown) {
            callback.error(XMLHttpRequest, textStatus, errorThrown);
          },
          success : function(data, textStatus) {
            callback.success(data, textStatus);
          },
          complete : function(XMLHttpRequest, textStatus) {
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
          return false;
        }
      }

      // Model for knockout(KO) binding
      function AppViewModel(rulesList) {
        var self = this;
        self.rulesBindingList = ko.observableArray(rulesList);
        self.eventsBindingList = ko.observableArray([]);
        self.parsedToString = function(item) {
          return JSON.stringify(item, null, 2);
        };
        // Removing the rule
        self.removeRule = function(data, event) {
          var context = ko.contextFor(event.target);
          self.rulesBindingList.splice(context.$index(), 1);
          if (self.rulesBindingList().length == 0) {
            $.jGrowl("Deleted all rule types, but we need atleast one Rule type, Here add default rule type", {
              sticky : false,
              theme : 'Error'
            });
            self.rulesBindingList.push(ruleTemplate);
          }
        };

        // Removing the events
        self.removeEvent = function(data, event) {
          var context = ko.contextFor(event.target);
          self.eventsBindingList.splice(context.$index(), 1);
          if (self.eventsBindingList().length == 0) {
            self.eventsBindingList.push({});
            $.jGrowl("Deleted all events, but we need atleast one event.", {
              sticky : false,
              theme : 'Error'
            });
            
          }
        };

        //This submit function for finding the aggregated object from the rules and events, This function internally call the ajax call
        self.submit = function() {
          var events = $("#eventsListID").val();
          var formRules = [];
          $('.formRules').each(function() {
            try {
              formRules.push(JSON.parse($(this).val()));
            } catch (e) {
              $.jGrowl("Invalid json rule format :\n" + $(this).val(), {
                sticky : false,
                theme : 'Error'
              });
              return false;
            }
          });
          
          var formEvents = [];
          $('.formEvents').each(function() {
            try {
              formEvents.push(JSON.parse($(this).val()));
            } catch (e) {
              $.jGrowl("Invalid json event format :\n" + $(this).val(), {
                sticky : false,
                theme : 'Error'
              });
              return false;
            }
          });

          var callback = {
            beforeSend : function() {
            },
            success : function(data, textStatus) {
              var returnData = data;
              if (returnData.length > 0) {
                $.jGrowl("Successfully aggregated object generated", {
                  sticky : false,
                  theme : 'Error'
                });

                $('#aggregatedresultData').text(JSON.stringify(data, null, 2));
                var divText = document.getElementById("aggregatedresult").outerHTML;
                var myWindow = window.open('', '', 'width=700,height=1000');
                var doc = myWindow.document;
                doc.open();
                doc.write(divText);
                doc.close();
              }
            },
            error : function(XMLHttpRequest, textStatus, errorThrown) {
              $.jGrowl("Failed to generate the aggregated object" + " Error: " + XMLHttpRequest.responseText, {
                sticky : false,
                theme : 'Error'
              });
            },
            complete : function() {
            }
          };
          
            var ajaxHttpSender = new AjaxHttpSender();
            //console.log(JSON.stringify(JSON.parse('{"listRulesJson":' + JSON.stringify(formRules) + ',"listEventsJson":' + events.toString() + '}')));
            ajaxHttpSender.sendAjax(frontendServiceUrl + "/rules/rule-check/aggregation", "POST", JSON.stringify(JSON.parse('{"listRulesJson":'
                + JSON.stringify(formRules) + ',"listEventsJson":' + JSON.stringify(formEvents) + '}')), callback);
        };

        // This function for adding rule
        self.addRule = function() {
          self.rulesBindingList.push(ruleTemplate);
        };
        // This function for adding rule
        self.addEvent = function() {
          self.eventsBindingList.push({});
        };
        return self;
      }

      var vm = new AppViewModel([]);
      ko.applyBindings(vm, $("#submitButton")[0]);
      vm.rulesBindingList.push(ruleTemplate);
      vm.eventsBindingList.push({});

      ko.applyBindings(vm, $("#testRulesDOMObject")[0]);
      ko.applyBindings(vm, $("#testEventsDOMObject")[0]);

      //Upload events list json data
      $(".container").on("click", "button.upload_rules", function(event) {
        event.stopPropagation();
        event.preventDefault();

        function validateJsonAndCreateSubscriptions(subscriptionFile) {
          var reader = new FileReader();
          reader.onload = function() {
            var fileContent = reader.result;
            var jsonLintResult = "";
            try {
              jsonLintResult = jsonlint.parse(fileContent);
            } catch (e) {
              $.alert("JSON Format Check Failed:\n" + e.name + "\n" + e.message);
              return false;
            }
            $.jGrowl('JSON Format Check Succeeded', {
              sticky : false,
              theme : 'Notify'
            });

            var rulesList = JSON.parse(fileContent);
            ko.cleanNode($("#testRulesDOMObject")[0]);
            ko.cleanNode($("#submitButton")[0]);
            $("#testRulesDOMObject").css('min-height', $(".navbar-sidenav").height() - 180);
            vm.rulesBindingList.removeAll();
            $('.rulesListDisplay > div:gt(0)').remove();
            vm.rulesBindingList = ko.observableArray(rulesList);
            ko.applyBindings(vm, $("#testRulesDOMObject")[0]);
            ko.applyBindings(vm, $("#submitButton")[0]);
          };
          reader.readAsText(subscriptionFile);
        }

        function createUploadWindow() {
          var pom = document.createElement('input');
          pom.setAttribute('id', 'uploadFile');
          pom.setAttribute('type', 'file');
          pom.setAttribute('name', 'upFile');
          pom.onchange = function uploadFinished() {
            var subscriptionFile = pom.files[0];
            validateJsonAndCreateSubscriptions(subscriptionFile);
          };
          if (document.createEvent) {
            var event = document.createEvent('MouseEvents');
            event.initEvent('click', true, true);
            pom.dispatchEvent(event);
          } else {
            pom.click();
          }
        }

        function createUploadWindowMSExplorer() {
          $('#upload_rules').click();
          var file = $('#upload_rules').prop('files')[0];
          validateJsonAndCreateSubscriptions(file);
        }

        // If MS Internet Explorer -> special handling for creating
        // download
        // file window.
        if (window.navigator.msSaveOrOpenBlob) {
          createUploadWindowMSExplorer();
        } else {
          // HTML5 Download File window handling
          createUploadWindow();
        }
      });

      //Upload list of events json data
      $(".container").on("click", "button.upload_events", function(event) {
        event.stopPropagation();
        event.preventDefault();

        function validateJsonAndCreateSubscriptions(subscriptionFile) {
          var reader = new FileReader();
          reader.onload = function() {
            var fileContent = reader.result;
            var jsonLintResult = "";
            try {
              jsonLintResult = jsonlint.parse(fileContent);
            } catch (e) {
              $.alert("JSON events Format Check Failed:\n" + e.name + "\n" + e.message);
              return false;
            }
            $.jGrowl('JSON events Format Check Succeeded', {
              sticky : false,
              theme : 'Notify'
            });
            var eventsList = JSON.parse(fileContent);
            ko.cleanNode($("#testEventsDOMObject")[0]);
            vm.eventsBindingList.removeAll();
            $('.eventsListDisplay > div:gt(0)').remove();
            vm.eventsBindingList = ko.observableArray(eventsList);
            ko.applyBindings(vm, $("#testEventsDOMObject")[0]);
          };
          reader.readAsText(subscriptionFile);
        }

        function createUploadWindow() {
          var pom = document.createElement('input');
          pom.setAttribute('id', 'uploadFile');
          pom.setAttribute('type', 'file');
          pom.setAttribute('name', 'upFile');
          pom.onchange = function uploadFinished() {
            var subscriptionFile = pom.files[0];
            validateJsonAndCreateSubscriptions(subscriptionFile);
          };
          if (document.createEvent) {
            var event = document.createEvent('MouseEvents');
            event.initEvent('click', true, true);
            pom.dispatchEvent(event);
          } else {
            pom.click();
          }
        }

        function createUploadWindowMSExplorer() {
          $('#upload_events').click();
          var file = $('#upload_events').prop('files')[0];
          validateJsonAndCreateSubscriptions(file);
        }

        // If MS Internet Explorer -> special handling for creating download
        // file window.
        if (window.navigator.msSaveOrOpenBlob) {
          createUploadWindowMSExplorer();
        } else {
          // HTML5 Download File window handling
          createUploadWindow();
        }
      });

      // Download the modified rule
      $('.container').on('click', 'button.download_rules', function() {
        var formRules = [];
        $('.formRules').each(function() {
          try {
            formRules.push(JSON.parse($(this).val()));
          } catch (e) {
            $.jGrowl("Invalid json format :\n" + $(this).val(), {
              sticky : false,
              theme : 'Error'
            });
            return false;
          }
        });
        if (formRules.length !== 0) {
          var jsonData = JSON.stringify(formRules, null, 2);
          downloadFile(jsonData, "application/json;charset=utf-8", "rules.json");
        } else {
          $.jGrowl("Data not available for download!", {
            sticky : false,
            theme : 'Error'
          });
        }

      });

      function getTemplate(name) {
        var request = new XMLHttpRequest();
        request.open("GET", '/download/' + name, true);
        request.responseType = "application/json;charset=utf-8";
        request.onload = function (event) {
           var jsonData = JSON.stringify(JSON.parse(request.response), null, 2);
           downloadFile(jsonData, "application/json;charset=utf-8", name + ".json");
        };
        request.send();
      }

      // Download the rules template
      $('.container').on('click', 'button.download_rules_template', function(event) {
		event.stopPropagation();
        event.preventDefault();
        getTemplate("rulesTemplate");
      });

      // Download the events template
      $('.container').on('click', 'button.download_events_template', function(event) {
		event.stopPropagation();
        event.preventDefault();
        getTemplate("eventsTemplate");
      });

    }
);