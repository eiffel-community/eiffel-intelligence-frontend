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
      loadTooltip();

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
            window.logMessages(XMLHttpRequest.responseText);
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
            window.logMessages("Deleted all rule types, but we need atleast one Rule type, Here add default rule type");
            self.rulesBindingList.push(ruleTemplate);
          }
        };

        // Removing the events
        self.removeEvent = function(data, event) {
          var context = ko.contextFor(event.target);
          self.eventsBindingList.splice(context.$index(), 1);
          if (self.eventsBindingList().length == 0) {
            self.eventsBindingList.push({});
            window.logMessages("Deleted all events, but we need atleast one event.");
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
                window.logMessages("Invalid json rule format :\n" + $(this).val());
                return false;
            }
          });
          
          var formEvents = [];
          $('.formEvents').each(function() {
            try {
              formEvents.push(JSON.parse($(this).val()));
            } catch (e) {
                window.logMessages("Invalid json event format :\n" + $(this).val());
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
                myWindow.focus();
              }
            },
            error : function(XMLHttpRequest, textStatus, errorThrown) {
                if (XMLHttpRequest.responseText == ""){
                    window.logMessages("Failed to generate the aggregated object, Error: Could not contact the backend server.");
                } else {
                    window.logMessages("Failed to generate the aggregated object, Error: " + XMLHttpRequest.responseText);
                }
            },
            complete : function() {
            }
          };
          
            var ajaxHttpSender = new AjaxHttpSender();
            ajaxHttpSender.sendAjax(frontendServiceUrl + "/rules/rule-check/aggregation", "POST", JSON.stringify(JSON.parse('{"listRulesJson":'
                + JSON.stringify(formRules) + ',"listEventsJson":' + JSON.stringify(formEvents) + '}')), callback);
        };

        // This function for adding rule
        self.addRule = function() {
          self.rulesBindingList.push(JSON.parse(JSON.stringify(ruleTemplate)));
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

      function validateRulesJsonAndCreateSubscriptions(subscriptionFile) {
	      var reader = new FileReader();
	      reader.onload = function() {
	        var fileContent = reader.result;
	        var jsonLintResult = "";
	        try {
	          jsonLintResult = jsonlint.parse(fileContent);
	        } catch (e) {
	            window.logMessages("JSON Format Check Failed:\n" + e.name + "\n" + e.message);
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
	        loadTooltip();
	      };
	    reader.readAsText(subscriptionFile);
      }

      function validateEventsJsonAndCreateSubscriptions(subscriptionFile) {
          var reader = new FileReader();
          reader.onload = function() {
            var fileContent = reader.result;
            var jsonLintResult = "";
            try {
              jsonLintResult = jsonlint.parse(fileContent);
            } catch (e) {
                window.logMessages("JSON events Format Check Failed:\n" + e.name + "\n" + e.message);
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
            loadTooltip();
          };
          reader.readAsText(subscriptionFile);
       }

        //Set onchange event on the input element "uploadRulesFile" and "uploadEventsFile"
        var pomRules = document.getElementById('uploadRulesFile');
        pomRules.onchange = function uploadFinished() {
            var subscriptionFile = pomRules.files[0];
            validateRulesJsonAndCreateSubscriptions(subscriptionFile);
            $(this).val("");
        };
        
        var pomEvents = document.getElementById('uploadEventsFile');
        pomEvents.onchange = function uploadFinished() {
            var subscriptionFile = pomEvents.files[0];
            validateEventsJsonAndCreateSubscriptions(subscriptionFile);
            $(this).val("");
        };
          
      //Upload events list json data
      $(".container").on("click", "button.upload_rules", function(event) {
        event.stopPropagation();
        event.preventDefault();

        function createRulesUploadWindow() {
          if (document.createEvent) {
            var event = document.createEvent('MouseEvents');
            event.initEvent('click', true, true);
            pomRules.dispatchEvent(event);
          } else {
            pomRules.click();
          }
        }
        
        function createUploadWindowMSExplorer() {
          $('#upload_rules').click();
          var file = $('#upload_rules').prop('files')[0];
          validateRulesJsonAndCreateSubscriptions(file);
        }

        // HTML5 Download File window handling
        createRulesUploadWindow();
      });

      //Upload list of events json data
      $(".container").on("click", "button.upload_events", function(event) {
        event.stopPropagation();
        event.preventDefault();

        function createUploadWindow() {
          if (document.createEvent) {
            var event = document.createEvent('MouseEvents');
            event.initEvent('click', true, true);
            pomEvents.dispatchEvent(event);
          } else {
            pomEvents.click();
          }
        }

        function createUploadWindowMSExplorer() {
          $('#upload_events').click();
          var file = $('#upload_events').prop('files')[0];
          validateEventsJsonAndCreateSubscriptions(file);
        }


        // HTML5 Download File window handling
        createUploadWindow();
      });

      // Download the modified rule
      $('.container').on('click', 'button.download_rules', function() {
        var formRules = [];
        $('.formRules').each(function() {
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

      function getTemplate(name) {
        var request = new XMLHttpRequest();
        request.open("GET", frontendServiceUrl + '/download/' + name, true);
        request.responseType = "application/json;charset=utf-8";
        request.onload = function (event) {
           if (this.responseText == ""){
               window.logMessages("Failed to download template, Error: Could not contact the backend server.");
           } else {
               var jsonData = JSON.stringify(JSON.parse(request.response), null, 2);
               downloadFile(jsonData, "application/json;charset=utf-8", name + ".json");
           }
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
      
   // Start to check is backend Test Rule service status
    	var isEnabled = true;
    	$.ajax({
    		url: frontendServiceUrl + "/rules/rule-check/testRulePageEnabled",
    		contentType : 'application/json; charset=utf-8',
    		type: 'GET',
    		error: function () {},
    		success: function (data) {
    			isEnabled = JSON.parse(ko.toJSON(data)).status;
    			if(isEnabled != true) {
    				displayOverlay("Test Rule service is not enabled! To enable it set the backend property [testaggregated.enabled] as [true]")}
    			},
    		complete: function () { }
    	});
    	// Finish to check backend Test Rule Service status
    	
    	function displayOverlay(text) {
          var overlay = document.createElement('div');
          overlay.setAttribute('id', 'overlay')
          overlay.setAttribute('class', 'testRulePage d-flex')
          var padding = document.createElement('div');
          padding.setAttribute('class','col-lg-3 col-md-1 d-none d-lg-block');
          var main = document.createElement('div');    
          main.setAttribute('class','col-lg-9 col-md-11 col-12 flexbox');
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
    }
);
