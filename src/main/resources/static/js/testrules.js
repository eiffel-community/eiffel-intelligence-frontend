// Global vars
var frontendServiceUrl;
var i = 0;
jQuery(document).ready(
    function() {

      frontendServiceUrl = $('#frontendServiceUrl').text();

      function AppViewModel(rulesList) {
        var self = this;
        self.rulesBindingList = ko.observableArray(rulesList);
        self.eventsBindingList = ko.observable();
        self.dropdown = ko.observableArray([ "EiffelArtifactCreatedEvent", "EiffelArtifactPublishedEvent", "EiffelConfidenceLevelModifiedEvent",
            "EiffelTestCaseStartedEvent", "EiffelTestCaseFinishedEvent", "EiffelCompositionDefinedEvent", "EiffelSourceChangeCreatedEvent",
            "EiffelSourceChangeSubmittedEvent" ]);
        self.backupdropdown = self.dropdown;
        self.parsedToString = function(item) {
          return JSON.stringify(item, null, 2);
        };

        self.removeDropdown = function(name) {
          var index = self.dropdown.indexOf(name);
          if (index !== -1) {
            self.dropdown.splice(index, 1);
          }
          return name;
        };

        self.removeRule = function(name) {
          self.rulesBindingList.remove(name);
          self.dropdown.push(name.Type);
        };
        self.submit = function() {
          var inputs = $("#eventsListID").val();
          var formRules = [];
          $('.formRules').each (function() {
            formRules.push($(this).val());
          });
          console.log("Rules : "+formRules.length);
          console.log(formRules.toString());
          console.log("Events : ");
          console.log(inputs.toString());
          var callback = {
            beforeSend : function() {
            },
            success : function(data, textStatus) {
              var returnData = [ data ];
              if (returnData.length > 0) {
                $.jGrowl("Successfully aggregated object generated", {
                  sticky : false,
                  theme : 'Error'
                });
                reload_table();
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
          // Perform AJAX
//          var ajaxHttpSender = new AjaxHttpSender();
//          ajaxHttpSender.sendAjax(frontendServiceUrl + "/rules/rule-check/aggregation", "POST", ko.toJSON("{'listRulesJson':"
//              + self.parsedToString(self.rulesBindingList()) + ",'listEventsJson':" + self.eventsBindingList.toString() + "}"), callback);

        };

        self.addRule = function(viewModel, event) {
          var newValue = event.target.value;
          if (newValue != '') {
            self.rulesBindingList.push({
              "TemplateName" : "",
              "Type" : newValue

            });
            self.removeDropdown(newValue);
          }
        }
        return self;
      }

      var vm = new AppViewModel([]);
      ko.applyBindings(vm, $("#submitButton")[0]);
      // ko.applyBindings(vm, $(".testTulesDOMObject")[0]);

      $(".container").on(
          "click",
          "button.upload_rules",
          function(event) {
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
                ko.cleanNode($("#testTulesDOMObject")[0]);
                ko.cleanNode($("#submitButton")[0]);

                vm.rulesBindingList.removeAll();
                $('.eventsListDisplay > div:gt(0)').remove();
                vm.rulesBindingList = ko.observableArray(rulesList);
                vm.dropdown([]);
                vm.dropdown = ko.observableArray([ "EiffelArtifactCreatedEvent", "EiffelArtifactPublishedEvent",
                    "EiffelConfidenceLevelModifiedEvent", "EiffelTestCaseStartedEvent", "EiffelTestCaseFinishedEvent",
                    "EiffelCompositionDefinedEvent", "EiffelSourceChangeCreatedEvent", "EiffelSourceChangeSubmittedEvent" ]);

                ko.applyBindings(vm, $("#testTulesDOMObject")[0]);
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
            ko.cleanNode($("#submitButton")[0]);
            $("#eventsListID").val(JSON.stringify(eventsList, null, 2));
            $(".textareaCustom").css('min-height', $(".navbar-sidenav").height() - 180);
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

    });