// Global vars
var frontendServiceUrl;
var i = 0;
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
        self.eventsBindingList = ko.observable();
        self.dropdown = ko.observableArray([ "EiffelArtifactCreatedEvent", "EiffelArtifactPublishedEvent", "EiffelConfidenceLevelModifiedEvent",
            "EiffelTestCaseStartedEvent", "EiffelTestCaseFinishedEvent", "EiffelCompositionDefinedEvent", "EiffelSourceChangeCreatedEvent",
            "EiffelSourceChangeSubmittedEvent" ]);
        self.backupdropdown = self.dropdown;
        self.parsedToString = function(item) {
          return JSON.stringify(item, null, 2);
        };

        //After adding a rule, this function remove the type from dropdown
        self.removeDropdown = function(name) {
          var index = self.dropdown.indexOf(name);
          if (index !== -1) {
            self.dropdown.splice(index, 1);
          }
          return name;
        };

        // Removing the rule
        self.removeRule = function(name) {
          self.rulesBindingList.remove(name);
          self.dropdown.push(name.Type);
        };
        
        //This submit function for finding the aggregated object from the rules and events, This function internally call the ajax call
        self.submit = function() {
          var events = $("#eventsListID").val();
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
          var eventsValid = isValidJSON(events.toString());
          if (!eventsValid) {
            alert("Events are not a valid json format");
          } else {
            var ajaxHttpSender = new AjaxHttpSender();
            //console.log(JSON.stringify(JSON.parse('{"listRulesJson":' + JSON.stringify(formRules) + ',"listEventsJson":' + events.toString() + '}')));
            ajaxHttpSender.sendAjax(frontendServiceUrl + "/rules/rule-check/aggregation", "POST", JSON.stringify(JSON.parse('{"listRulesJson":'
                + JSON.stringify(formRules) + ',"listEventsJson":' + events.toString() + '}')), callback);
          }
        };

        // This function for adding rule
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
      vm.rulesBindingList.push({
        "TemplateName" : "",
        "Type" : "EiffelArtifactCreatedEvent"

      });
      vm.removeDropdown("EiffelArtifactCreatedEvent");
      ko.applyBindings(vm, $("#testTulesDOMObject")[0]);

      //Upload events list json data
      $(".container").on("click","button.upload_rules", function(event) {
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
          var contentType = "application/json;charset=utf-8";
          var jsonData = JSON.stringify(formRules, null, 2);
          var fileName = "rules.json"

          function downloadFile(data, type, title) {
            var link = document.createElement('a');
            link.setAttribute("href", "data:" + type + "," + encodeURIComponent(data));
            link.setAttribute("download", fileName);
            link.setAttribute("class", "hidden");
            link.click();
          }

          function downloadFileMSExplorer(data, type, title) {
            var blob = new Blob([ data ], {
              type : type
            });
            window.navigator.msSaveOrOpenBlob(blob, title);
          }

          if (window.navigator.msSaveOrOpenBlob) {
            downloadFileMSExplorer(jsonData, contentType, fileName);
          } else {
            downloadFile(jsonData, contentType, fileName);
          }
        }
        else{
          $.jGrowl("Data not available for download!", {
            sticky : false,
            theme : 'Error'
          });
        }

      });
    });