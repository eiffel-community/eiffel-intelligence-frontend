// Global vars
var save_method;
var table;
var frontendServiceUrl;
var defaultFormKeyValuePair = { "formkey": "", "formvalue": "" };
var defaultFormKeyValuePairAuth = { "formkey": "Authorization", "formvalue": "" };

jQuery(document).ready(function () {

    $('.modal-dialog').draggable({ handle: ".modal-header", cursor: 'move' });

    // Fetch injected URL from DOM
    frontendServiceUrl = $('#frontendServiceUrl').text();

    // /Start ## Global AJAX Sender function ##################################
    var AjaxHttpSender = function () { };

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
                window.logMessages(XMLHttpRequest.responseText);
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

    // Check EI Backend Server Status ########################################
    var backendStatus = false;
    function checkBackendStatus() {
        var EIConnBtn = document.getElementById("btnEIConnection");
        if (EIConnBtn == null) {
            return;
        }
        var red = "#ff0000";
        var green = "#00ff00";
        $.ajax({
            url: frontendServiceUrl + "/auth/checkStatus",
            type: "GET",
            contentType: "application/string; charset=utf-8",
            dataType: "text",
            cache: false,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                if (XMLHttpRequest.status == 401) {
                    doIfUserLoggedOut();
                    EIConnBtn.style.background = green;
                    backendStatus = true;
                } else {
                    EIConnBtn.style.background = red;
                    backendStatus = false;
                }
            },
            success: function (data, textStatus) {
                EIConnBtn.style.background = green;
                backendStatus = true;
            }
        });
    }

    function doIfUserLoggedIn() {
        var currentUser = localStorage.getItem("currentUser");
        if (currentUser != "") {
            $("#ldapUserName").text(currentUser);
            $("#logoutBlock").show();
            $(".show_if_authorized").show();
        }
    }
    function doIfUserLoggedOut() {
        localStorage.removeItem("currentUser");
        $("#ldapUserName").text("Guest");
        $("#loginBlock").show();
        $("#logoutBlock").hide();
        $(".show_if_authorized").hide();
        localStorage.setItem('errorsStore', []);
    }

    // Check if EI Backend Server is online every X seconds
    window.setInterval(function () { checkBackendStatus(); }, 15000);

    // Check if EI Backend Server is online when Status Connection button is pressed.
    $("#btnEIConnection").click(function () {
        checkBackendStatus();
    });
    // END OF EI Backend Server check #########################################

    // /Start ## Knockout ####################################################

    var lastPressedRestPostBodyMediaType = "";
    // Subscription model
    function subscription_model(data) {
        this.created = ko.observable(data.created);
        this.notificationMeta = ko.observable(data.notificationMeta).extend({ notify: 'always' });
        this.notificationType = ko.observable(data.notificationType);
        this.restPostBodyMediaType = ko.observable(data.restPostBodyMediaType);
        this.notificationMessageRawJson = ko.observable(data.notificationMessageRawJson).extend({ notify: 'always' });
        this.notificationMessageKeyValues = ko.observableArray(data.notificationMessageKeyValues).extend({ notify: 'always' });
        this.notificationMessageKeyValuesAuth = ko.observableArray(data.notificationMessageKeyValuesAuth);
        this.repeat = ko.observable(data.repeat);
        this.requirements = ko.observableArray(data.requirements);
        this.subscriptionName = ko.observable(data.subscriptionName).extend({ notify: 'always' });
        this.aggregationtype = ko.observable(data.aggregationtype);
        this.authenticationType = ko.observable(data.authenticationType);
        this.userName = ko.observable(data.userName);
        this.password = ko.observable(data.password);

        // Default to REST_POST
        if (this.notificationType() == "" || this.notificationType() == null) {
            this.notificationType = ko.observable("REST_POST");
        }

        if (this.notificationType() == "REST_POST") {
            vm.restPost(true);
        } else {
            vm.restPost(false);
        }

        // Default to Repeat off
        if (this.repeat() == "" || this.repeat() == null || this.repeat == undefined) {
            this.repeat = ko.observable(false);
        }

        // Default to RAW BODY
        if (this.restPostBodyMediaType() == "application/x-www-form-urlencoded") {
            vm.formpostkeyvaluepairs(true);
        } else {
            this.restPostBodyMediaType = ko.observable("application/json");
            vm.formpostkeyvaluepairs(false);
        }

        // Subscribe START
        // Subscribe notificationType
        this.notificationType.subscribe(function (new_value) {
            var allowEmpty = true;
            var validateOnlyKey = false;
            $('#invalidNotificationMeta').hide();
            vm.formpostkeyvaluepairs(false);
            if (new_value == "REST_POST") {
                vm.restPost(true);
                if (lastPressedRestPostBodyMediaType == "application/x-www-form-urlencoded") {
                    vm.formpostkeyvaluepairs(true);
                    validateNotificationMessageKeyValues(vm.subscription()[0].notificationMessageKeyValues(), validateOnlyKey, allowEmpty)
                } else {
                    validateMessageRawJson(this.notificationMessageRawJson(), allowEmpty);
                }
            } else {
                validateMessageRawJson(this.notificationMessageRawJson(), allowEmpty);
                vm.restPost(false);
            }
            validateNotificationMeta(this.notificationMeta(), allowEmpty);
        }, this);

        // Subscribe restPostBodyMediaType
        this.restPostBodyMediaType.subscribe(function (new_value) {
            var validateOnlyKey = false;
            var allowEmpty = true;
            // Remember last restPostMediaType, when switching from MAIL we get back to the last used.
            lastPressedRestPostBodyMediaType = new_value;
            if (new_value == "application/x-www-form-urlencoded") {
                validateNotificationMessageKeyValues(vm.subscription()[0].notificationMessageKeyValues(), validateOnlyKey, allowEmpty)
                vm.formpostkeyvaluepairs(true);
            } else {
                vm.formpostkeyvaluepairs(false);
                validateMessageRawJson(this.notificationMessageRawJson(), allowEmpty);
            }
        }, this);

        // Subscribe subscriptionName
        this.subscriptionName.subscribe(function (name_input) {
            validateName(name_input);
        });

        this.notificationMeta.subscribe(function (notificationMeta) {
            var allowEmpty = false;
            validateNotificationMeta(notificationMeta, allowEmpty);
        });

        this.notificationMessageRawJson.subscribe(function (jsonData) {
            validateMessageRawJson(jsonData)
        });
        // Subscribe END
    }

    function formdata_model(formdata) {
        this.formkey = ko.observable(formdata.formkey);
        this.formvalue = ko.observable(formdata.formvalue);

        this.formkey.subscribe(function (newText) {
            var validateOnlyKey = true;
            validateNotificationMessageKeyValues(vm.subscription()[0].notificationMessageKeyValues(), validateOnlyKey)
        });

        this.formvalue.subscribe(function (newText) {
            validateNotificationMessageKeyValues(vm.subscription()[0].notificationMessageKeyValues())
        });
    }

    function conditions_model(condition) {
        this.conditions = ko.observableArray(condition);
    }

    function jmespath_model(jmespath) {
        this.jmespath = ko.observable(jmespath.jmespath);
    }

    // ViewModel - SubscriptionViewModel
    var SubscriptionViewModel = function () {
        var self = this;
        self.subscription = ko.observableArray([]);
        self.subscription_templates_in = ko.observableArray([
                { "text": "Jenkins Pipeline Parameterized Job Trigger", value: "templatejenkinsPipelineParameterizedBuildTrigger" },
                { "text": "REST POST (Raw Body : JSON)", value: "templateRestPostJsonRAWBodyTrigger" },
                { "text": "Mail Trigger", value: "templateEmailTrigger" }
            ]);
        self.choosen_subscription_template = ko.observable();
        self.authenticationType = ko.observable();
        self.restPost = ko.observable(false);
        self.formpostkeyvaluepairs = ko.observable(false);
        self.mode = ko.observable("");
        self.showPassword = ko.observable(false);
        self.setShowPassword = function (boolean){
            self.showPassword(boolean);
        }
        self.formpostkeyvaluepairsAuth = ko.observable(false);
        self.notificationType_in = ko.observableArray([
                {"value": "REST_POST", "label": "REST_POST", "id": "restPostRadio"},
                {"value": "MAIL", "label": "MAIL", "id": "mailRadio"}
            ]);
        self.restPostBodyMediaType_in = ko.observableArray([
                {"value": "application/x-www-form-urlencoded", "label": "FORM/POST Parameters", "id": "keyValueRadio"},
                {"value": "application/json", "label": "RAW BODY: JSON",  "id": "appJsonRadio"}
            ]);
        self.authenticationType_in = ko.observableArray([
                { "text": "NO_AUTH", value: "NO_AUTH" },
                { "text": "BASIC_AUTH", value: "BASIC_AUTH" }
            ]);
        self.repeat_in = ko.observableArray([
                { "value": true, "label": "Activate Repeat" }
            ]);

        self.add_requirement = function (data, event) {
            var conditions_array = [];
            conditions_array.push(new jmespath_model({ "jmespath": ko.observable("") }));
            self.subscription()[0].requirements().push(new conditions_model(conditions_array));

            // Force update
            var data = self.subscription().slice(0);
            self.subscription([]);
            self.subscription(data);
            self.subscription.valueHasMutated();
            loadTooltip();
        };

        self.choosen_subscription_template.subscribe(function (template_var) {
            if (self.choosen_subscription_template() != null) { // only execute if value exists
                json_obj_clone = JSON.parse(JSON.stringify(template_vars[template_var]));
                populate_json(json_obj_clone, "add");
            }
        });

        self.addNotificationMsgKeyValuePair = function (data, event) {
            self.subscription()[0].notificationMessageKeyValues.push(new formdata_model(defaultFormKeyValuePair));
        };

        self.getUTCDate = function (epochtime) {
            var date = new Date(epochtime);
            var resolvedOptions = Intl.DateTimeFormat().resolvedOptions();
            var options = {
                    year: 'numeric',
                    month: 'short',
                    day: 'numeric',
                    hour: 'numeric',
                    minute: 'numeric',
                    second: 'numeric',
                    hour12: false,
                    timeZone: resolvedOptions.timeZone,
                    timeZoneName: 'short'
            };
            return date.toLocaleDateString(resolvedOptions.locale, options);  // Is now a date (in client time zone)
        }

        self.add_requirement = function (data, event) {

            var conditions_array = [];
            conditions_array.push(new jmespath_model({ "jmespath": ko.observable("") }));
            self.subscription()[0].requirements().push(new conditions_model(conditions_array));
            // Force update
            var data = self.subscription().slice(0);
            self.subscription([]);
            self.subscription(data);
            self.subscription.valueHasMutated();
            closeTooltip();
            loadTooltip();
        };

        self.add_condition = function (data, event, requirement_index) {
            self.subscription()[0].requirements()[ko.toJSON(requirement_index)].conditions().push(new jmespath_model({ "jmespath": ko.observable("") }));
            // Force update
            var data = self.subscription().slice(0);
            self.subscription([]);
            self.subscription(data);
            self.subscription.valueHasMutated();
            closeTooltip();
            loadTooltip();
        };

        self.delete_condition = function (data, event, requirement_item, condition_index, requirement_index) {
            self.subscription()[0].requirements()[ko.toJSON(requirement_index)].conditions.remove(data);
            if (self.subscription()[0].requirements()[ko.toJSON(requirement_index)].conditions().length <= 0) {
                self.subscription()[0].requirements.remove(self.subscription()[0].requirements()[ko.toJSON(requirement_index)]);
            }
        };

        self.delete_NotificationMsgKeyValuePair = function (data, event, index) {
            if (self.subscription()[0].notificationMessageKeyValues().length > 1) {
                self.subscription()[0].notificationMessageKeyValues.remove(self.subscription()[0].notificationMessageKeyValues()[ko.toJSON(index)]);
            }
        };

        self.delete_BulkNotificationMsgKeyValuePair = function () {
            $.each(self.subscription()[0].notificationMessageKeyValues(), function (index, value) {
                if (self.subscription()[0].notificationMessageKeyValues().length > 1) {
                    self.subscription()[0].notificationMessageKeyValues.remove(self.subscription()[0].notificationMessageKeyValues()[ko.toJSON(index)]);
                }
            });
        };
    };

    // Start to check is backend secured
    var isSecured = false;
    $.ajax({
        url: frontendServiceUrl + "/auth",
        contentType: 'application/json; charset=utf-8',
        type: 'GET',
        error: function () { },
        success: function (data) {
            isSecured = JSON.parse(ko.toJSON(data)).security;
            if (isSecured == true) {
                doIfUserLoggedIn();
            }
        },
        complete: function () {
            checkBackendStatus();
        }
    });
    // Finish to check is backend secured

    // Cleanup old ViewModel and Knockout Obeservables from previous page load.
    var observableObject = $('#ViewModelDOMObject')[0];
    ko.cleanNode(observableObject);
    // Apply bindings
    var vm = new SubscriptionViewModel();
    ko.applyBindings(vm, observableObject);

    // /Stop ## Knockout #####################################################

    // /Start ## Datatables ##################################################
    var currentUser = localStorage.getItem("currentUser");
    table = $('#table').DataTable({
        "responsive": true,
        "autoWidth": false,
        "processing": true, //Feature control the processing indicator.
        "serverSide": false, //Feature control DataTables' server-side processing mode.
        "fixedHeader": true,
        "order": [], //Initial no order.
        "searching": true,
        // Load data for the table's content from an Ajax source
        "ajax": {
            "url": frontendServiceUrl + "/subscriptions",
            "type": "GET",
            "dataSrc": "",   // Flat structure from EI backend REST API
            "error": function () { }
        },
        //Set column definition initialisation properties.
        "columnDefs": [
            {
                "targets": [0],
                "orderable": false,
                "className": "control",
                "data":"subscriptionName",
                "render": function (data, type, row, meta) {
                    return '';
                }
            },
            {
                "targets": [1],
                "orderable": false,
                "data": "subscriptionName",
                "title": '<input type="checkbox" id="check-all" />',
                "render": function (data, type, row, meta) {
                    return '<input type="checkbox" class="data-check" value="' + data + '">';
                }
            },
            {
                "targets": [2],
                "orderable": true,
                "title": "Owner",
                "data": "ldapUserName",
                "defaultContent": ""
            },
            {
                "targets": [3],
                "orderable": true,
                "title": "SubscriptionName",
                "data": "subscriptionName"
            },
            {
                "targets": [4],
                "orderable": true,
                "title": "Date",
                "data": "created",
                "mRender": function (data, type, row, meta) {
                    return vm.getUTCDate(data);
                }
            },
            {
                "targets": [5],
                "orderable": true,
                "title": "NotificationType",
                "data": "notificationType"
            },
            {
                "targets": [6],
                "orderable": true,
                "title": "NotificationMeta",
                "data": "notificationMeta"
            },
            {
                "targets": [7],
                "orderable": true,
                "title": "Repeat",
                "data": "repeat"
            },
            {
                "targets": [8],
                "className": "sub-action-column",
                "orderable": false,
                "title": "Action",
                "data": null,
                "render": function (data, type, row, meta) {

                    if (isSecured == false || (row.ldapUserName == currentUser && row.ldapUserName != null)) {
                        return '<button id="view-' + data.subscriptionName + '" class="btn btn-sm btn-success view_record">View</button> '
                            + '<button id="edit-' + data.subscriptionName + '" class="btn btn-sm btn-primary edit_record">Edit</button> '
                            + '<button id="delete-' + data.subscriptionName + '" class="btn btn-sm btn-danger delete_record">Delete</button>';
                    } else {
                        return '<button id="view-' + data.subscriptionName + '" class="btn btn-sm btn-success view_record">View</button>';
                    }
                }
            }
        ],
        "initComplete": function () {
            if (isSecured == false) {
                table.column(2).visible(false);
            }
        }
    });

    $("#sidenavCollapse").click(function() {
        table.responsive.rebuild();
        table.responsive.recalc();
    });
    // /Stop ## Datatables ##################################################

    // /Start ## check all subscriptions ####################################
    $("#check-all").click(function () {
        $(".data-check").prop('checked', $(this).prop('checked'));
    });
    // /Stop ## check all subscriptions #####################################

    // /Start ## Add Subscription ########################################
    $("#addSubscription").click(function () {
        vm.choosen_subscription_template(null);
        json_obj_clone = JSON.parse(JSON.stringify(default_json_empty));
        populate_json(json_obj_clone, "add");
    });
    // /Stop ## Add Subscription ############################################

    // /Start ## Reload Table################################################
    $("#reloadButton").click(function () {
        reload_table();
    });
    // /Stop ## Reload Table#################################################

    // /Start ## Bulk delete#################################################
    $("#bulkDelete").click(function () {
        var subscriptionsToDelete = [];
        var data = table.rows().nodes();
        $.each(data, function (index, value) {
            if ($(this).find('input').prop('checked') == true) {
                subscriptionsToDelete.push(table.row(index).data().subscriptionName)
            }
        });

        // Check if no Subscription has been marked to be deleted.
        if (subscriptionsToDelete.length < 1) {
            window.logMessages("No subscriptions has been marked to be deleted.");
            return;
        }

        var subscriptionsToDeleteString = "";
        for (i = 0; i < subscriptionsToDelete.length; i++) {
            subscriptionsToDeleteString += subscriptionsToDelete[i] + "\n";
        }

        var callback = {
            beforeSend: function () {
            },
            success: function (data, textStatus) {
                $.jGrowl('Subscriptions deleted!', {
                    sticky: false,
                    theme: 'Notify'
                });
                //if success reload ajax table
                $('#modal_form').modal('hide');
                reload_table();
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                window.logMessages(XMLHttpRequest.responseText);
                reload_table();
                var responseJSON = JSON.parse(XMLHttpRequest.responseText);
                for (var i = 0; i < responseJSON.length; i++) {
                    $.jGrowl(responseJSON[i].subscription + " :: " + responseJSON[i].reason, { sticky: true, theme: 'Error' });
                }
            },
            complete: function () {
            }
        };

        $.confirm({
            title: 'Confirm!',
            content: 'Are you sure you want to delete these subscriptions?<pre>' + subscriptionsToDeleteString,
            buttons: {
                confirm: function () {
                    var ajaxHttpSender = new AjaxHttpSender();
                    // replace all /n with comma
                    subscriptionsToDeleteString = subscriptionsToDeleteString.replace(new RegExp('\n', 'g'), ',').slice(0, -1);
                    ajaxHttpSender.sendAjax(frontendServiceUrl + "/subscriptions/" + subscriptionsToDeleteString, "DELETE", null, callback);
                },
                cancel: function () {
                }
            }
        });
    });
    // /Stop ## Bulk delete##################################################

    function getTemplate() {
        var req = new XMLHttpRequest();
        req.open("GET", frontendServiceUrl + '/download/subscriptionsTemplate', true);
        req.responseType = "application/json;charset=utf-8";
        req.onload = function (event) {
            if (this.responseText == ""){
                window.logMessages("Failed to download template, Error: Could not contact the backend server.");
            } else {
                var jsonData = JSON.stringify(JSON.parse(req.response), null, 2);
                downloadFile(jsonData, "application/json;charset=utf-8", "subscriptionsTemplate.json");
            }
        };
        req.send();
    }

    // /Start ## get_subscription_template #################################################
    $("#getTemplateButton").click(function () {
        getTemplate();
    });
    // /END ## get_subscription_template #################################################

    function validateJsonAndCreateSubscriptions(subscriptionFile) {
        var reader = new FileReader();
        reader.onload = function () {
            var fileContent = reader.result;
            var isValid = validateJsonString(fileContent, true);
            if (!isValid) {
                return false;
            }
            $.jGrowl('JSON Format Check Succeeded', {
                sticky: false,
                theme: 'Notify'
            });
            var subscriptionJsonList = JSON.parse(fileContent);
            tryToCreateSubscription(subscriptionJsonList);
        };
        reader.readAsText(subscriptionFile);
    }

    function validateJsonString(jsonString, logError) {
        try {
            var jsonLintResult = jsonlint.parse(jsonString);
            return true;
        } catch (e) {
            if (logError) {
                window.logMessages("JSON Format Check Failed:\n" + e.name + "\n" + e.message);
            }
            return false;
        }
    }

    var pom = document.getElementById('upload_sub');
    pom.onchange = function uploadFinished() {
        var subscriptionFile = pom.files[0];
        validateJsonAndCreateSubscriptions(subscriptionFile);
    };
    function tryToCreateSubscription(subscriptionJson) {
        // Send Subscription JSON file to Spring MVC
        // AJAX Callback handling
        var callback = {
            beforeSend: function () {
            },
            success: function (data, textStatus) {
                var returnData = [data];
                if (returnData.length > 0) {
                    $.jGrowl("Subscriptions were successfully created.", {
                        sticky: false,
                        theme: 'Error'
                    });
                    reload_table();
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                window.logMessages("Failed to create Subscriptions.");
                reload_table();
                $.jGrowl("Failed to create Subscriptions.", { sticky: false, theme: 'Error' });
                var responseJSON = JSON.parse(XMLHttpRequest.responseText);
                for (var i = 0; i < responseJSON.length; i++) {
                    $.jGrowl(responseJSON[i].subscription + " :: " + responseJSON[i].reason, { sticky: true, theme: 'Error' });
                }
            },
            complete: function () {
            }
        };
        // Perform AJAX
        var ajaxHttpSender = new AjaxHttpSender();
        ajaxHttpSender.sendAjax(frontendServiceUrl + "/subscriptions", "POST", ko.toJSON(subscriptionJson), callback);
    }

    // /Start ## upload_subscriptions #################################################
    $("#uploadSubscription").click(function () {
        function createUploadWindow() {
            //            var pom = document.createElement('input');
            //            pom.setAttribute('id', 'uploadFile');
            //            pom.setAttribute('type', 'file');
            //            pom.setAttribute('name', 'upFile');
            //            pom.onchange = function uploadFinished() {
            //            	var subscriptionFile = pom.files[0];
            //            	validateJsonAndCreateSubscriptions(subscriptionFile);
            //        	};
            if (document.createEvent) {
                var event = document.createEvent('MouseEvents');
                event.initEvent('click', true, true);
                pom.dispatchEvent(event);
            }
            else {
                pom.click();
            }
        }

        function createUploadWindowMSExplorer() {
            $('#upload_subscription_file').click();
            var file = $('#upload_subscription_file').prop('files')[0];
            validateJsonAndCreateSubscriptions(file);
        }

        // If MS Internet Explorer -> special handling for creating download file window.
        if (window.navigator.msSaveOrOpenBlob) {
            createUploadWindowMSExplorer();
        }
        else {
            // HTML5 Download File window handling
            createUploadWindow();
        }
    });
    // /END ## upload_subscriptions #################################################

    // /Start ## Reload Datatables ###########################################
    function reload_table() {
        table.ajax.reload(null, false); //reload datatable ajax
    }
    // /Stop ## Reload Datatables ############################################

    function get_subscription_data(object, mode, event) {
        event.stopPropagation();
        event.preventDefault();
        // Get tag that contains subscriptionName
        var id = $(object).attr("id").split("-")[1];
        // AJAX Callback handling
        var callback = {
            beforeSend: function () { },
            success: function (data, textStatus) {
                populate_json(data, mode);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                window.logMessages("Error: " + XMLHttpRequest.responseText);
            },
            complete: function () { }
        };
        // Perform AJAX
        var ajaxHttpSender = new AjaxHttpSender();
        ajaxHttpSender.sendAjax(frontendServiceUrl + "/subscriptions/" + id, "GET", null, callback);
    }

    // /Start ## Edit Subscription ###########################################
    $('#table').on('click', 'tbody tr td button.edit_record', function (event) {
        get_subscription_data(this, "edit", event);
    });

    // /Stop ## Edit Subscription ###########################################

    // /Start ## View Subscription ###########################################
    $('#table').on('click', 'tbody tr td button.view_record', function (event) {
        get_subscription_data(this, "view", event);
    });
    // /Stop ## View Subscription ###########################################

    // /Start ## populate JSON  ###########################################
    function populate_json(data, save_method_in) {
        vm.mode(save_method_in);

        if (save_method_in == "edit" || save_method_in == "view"){
            vm.showPassword(false);
        } else {
            vm.showPassword(true);
        }
        var returnData = [data];
        if (returnData.length > 0) {
            vm.subscription([]);
            // Map JSON to Model and observableArray
            var mappedPackageInfo = $.map(returnData, function (item) {
                if (item.foundSubscriptions != null) {
                    item = item.foundSubscriptions;
                }
                // Defining Observable on all parameters in Requirements array(which is defined as ObservableArray)
                for (i = 0; i < item[0].requirements.length; i++) {
                    var conditions_array = [];
                    for (k = 0; k < item[0].requirements[i].conditions.length; k++) {
                        var jmespath_temp = item[0].requirements[i].conditions[k].jmespath;
                        conditions_array.push(new jmespath_model({ "jmespath": ko.observable(jmespath_temp) }));
                    }
                    item[0].requirements[i] = new conditions_model(conditions_array);
                }

                var notificationMessageRawJsonShouldBeReplacedWithFormValue = (
                    item[0].notificationMessageKeyValues.length == 1 &&
                    item[0].notificationMessageKeyValues[0].formkey == "");
                if (notificationMessageRawJsonShouldBeReplacedWithFormValue) {
                    item[0].notificationMessageRawJson = item[0].notificationMessageKeyValues[0].formvalue;
                    item[0].notificationMessageKeyValues[0] = new formdata_model({"formkey":"","formvalue":""});
                } else {
                    item[0].notificationMessageRawJson = "";
                    for (i = 0; i < item[0].notificationMessageKeyValues.length; i++) {
                        item[0].notificationMessageKeyValues[i] = new formdata_model(item[0].notificationMessageKeyValues[i])
                    }
                }

                return new subscription_model(item[0]);
            });
            // Load data into observable array
            vm.subscription(mappedPackageInfo);
            // Force update
            vm.subscription()[0].restPostBodyMediaType.valueHasMutated();
            loadTooltip();
            $('#modal_form').modal('show');
            if (save_method_in === "edit") {
                title_ = 'Edit Subscription';
                addEditMode();
            } else if (save_method_in === "add") {

                title_ = 'Add Subscription';
                addEditMode();
            } else {
                title_ = 'View Subscription';
                viewMode();
            }
            $('.modal-title').text(title_);
            save_method = save_method_in;
            $('#modal_form').on('hidden.bs.modal', function() {
                $('.text-danger').hide();
            });
        }
    }

    function addEditMode() {
        $('#modal_form :button').show();
        $('#modal_form :input').prop("disabled", false);
    }
    function viewMode() {
        $('#modal_form :button').hide();
        $('#modal_form :input').prop("disabled", true);
        $('#modal_form .close').show();
        $('#modal_form .close').prop("disabled", false);
        $('.text-danger').hide();
    }
    // /Stop ## pupulate JSON  ###########################################

    function validateName(subscriptionName) {
        var error = false;
        $('#invalidSubscriptionName').hide();
        $('#subscriptionNameInput').removeClass("is-invalid");
        $('#errorExists').hide();

        // Validate SubscriptionName field
        if (subscriptionName == "") {
            $('#invalidSubscriptionName').text("SubscriptionName must not be empty");
            $('#subscriptionNameInput').addClass("is-invalid");
            error = true;
        }

        // /(\W)/ Is a regex that matches anything that is not [A-Z,a-z,0-8] and _.
        var regExpression = /(\W)/g;
        if ((regExpression.test(subscriptionName))) {
            var invalidLetters = subscriptionName.match(regExpression);
            $('#invalidSubscriptionName').text(
                "Only letters, numbers and underscore allowed! "
                + "Invalid characters: [" + invalidLetters + "]");
            $('#subscriptionNameInput').addClass("is-invalid");
            error = true;
        }
        if (error) {
            $('#invalidSubscriptionName').show();
        }
        return error;
    }

    function validateNotificationMeta(notificationMeta, allowEmpty) {
        var error = false;
        $('#invalidNotificationMeta').hide();
        $('#notificationMeta').removeClass("is-invalid");
        $('#errorExists').hide();

        var notoficationMetaIsEmpty = (
            !allowEmpty && notificationMeta == "" || !allowEmpty && notificationMeta.replace(/\s/g, "") == '""');
        if (notoficationMetaIsEmpty) {
            $('#invalidNotificationMeta').text("NotificationMeta must not be empty");
            error = true;
        } else if (vm.restPost()) {
            //validate url not implemented yet.
        } else if (!vm.restPost()) {
            // Validate email
            var regExpression = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            var isInvalidEmailAddress = (!regExpression.test(notificationMeta) && notificationMeta != "");
            if (isInvalidEmailAddress) {
                $('#invalidNotificationMeta').text("Not a valid email.");
                error = true;
            }
        }
        if (error) {
            $('#notificationMeta').addClass("is-invalid");
            $('#invalidNotificationMeta').show();
        }
        return error;
    }

    function validateMessageRawJson(jsonData, allowEmpty) {
        var error = false;
        $('#notificationMessageKeyError').hide();
        $('#notificationMessageValuesError').hide();
        $('#notificationMessageValuesJsonError').hide();
        $('#invalidNotificationMessageRawJson').hide();
        $('#notificationMessageRawJson').removeClass("is-invalid");
        $('#errorExists').hide();

        var inputDataIsEmpty = (!allowEmpty && jsonData == "");
        if (inputDataIsEmpty) {
            $('#invalidNotificationMessageRawJson').text("The raw body field must not be empty.");
            error = true;
        }

        var inpurDataIsNotValidJson = (jsonData != "" && !validateJsonString(jsonData));
        if (inpurDataIsNotValidJson) {
            $('#invalidNotificationMessageRawJson').text("Input is not valid JSON.");
            error = true;
        }

        if (error) {
            $('#invalidNotificationMessageRawJson').show();
            $('#notificationMessageRawJson').addClass("is-invalid");
        }

        return error;
    }

    function validateNotificationMessageKeyValues(notificationMessageKeyValuesArray, validateOnlyKey, allowEmpty) {
        var error = false;
        $('#notificationMessageKeyError').hide();
        $('#notificationMessageValuesError').hide();
        $('#notificationMessageValuesJsonError').hide();
        $('#invalidNotificationMessageRawJson').hide();
        $('#errorExists').hide();

        for (i = 0; i < notificationMessageKeyValuesArray.length; i++) {
            var testKey = notificationMessageKeyValuesArray[i].formkey().replace(/ /g, ""); //Do validation without spaces;
            var testValue = notificationMessageKeyValuesArray[i].formvalue().replace(/ /g, ""); //Do validation without spaces

            $('#formvalue_' + i).removeClass("is-invalid");
            $('#formkey_' + i).removeClass("is-invalid");

            var testValueShouldBeValidatedAsJson = (
                String(testKey).toLowerCase().replace(/"/g, "") == "json" && testValue != "");
            if (testValueShouldBeValidatedAsJson) {

                // check value field for valid JSON
                var testValueIsNotValidJson = !validateJsonString(testValue);
                if (testValueIsNotValidJson) {
                    $('#formvalue_' + i).addClass("is-invalid");
                    $('#formkey_' + i).addClass("is-invalid");
                    $('#notificationMessageValuesJsonError').text("A key indicates JSON but value is not valid JSON!");
                    $('#notificationMessageValuesJsonError').show();
                    error = true;
                }
            }

            var testKeyIsEmpty = (testKey == "" && !allowEmpty);
            if (testKeyIsEmpty) {
                $('#formkey_' + i).addClass("is-invalid");
                $('#notificationMessageKeyError').text("One or more keys are not set!");
                $('#notificationMessageKeyError').show();
                error = true;
            }

            var testValueIsEmpty = (!validateOnlyKey && !allowEmpty && testValue == "");
            if (testValueIsEmpty) {
                $('#formvalue_' + i).addClass("is-invalid");
                $('#notificationMessageValuesError').text("One or more values are not set!");
                $('#notificationMessageValuesError').show();
                error = true;
            }
        }
        return error;
    }

    function validateFormPostData() {
        // Validations start
        var error = false;
        $('.text-danger').hide();

        // Validate subscription name field
        var subscriptionName = String(vm.subscription()[0].subscriptionName());
        var subscriptionNameIsInvalid = validateName(subscriptionName);
        if (subscriptionNameIsInvalid) {
            error = true;
        }

        // Validate notificationMeta field
        var notificationMeta = String(vm.subscription()[0].notificationMeta());
        var notificationMetaIsInvalid = validateNotificationMeta(notificationMeta);
        if (notificationMetaIsInvalid) {
            error = true;
        }

        // Validate notification message(s)
        if (vm.formpostkeyvaluepairs()) {
            // Validate notificationMessageKeyValues field
            var notificationMessageKeyValuesArray = vm.subscription()[0].notificationMessageKeyValues();
            var notificationMessageKeyValuesArrayIsInvalid = validateNotificationMessageKeyValues(notificationMessageKeyValuesArray);
            if (notificationMessageKeyValuesArrayIsInvalid) {
                error = true;
            }
        } else {
            // Validate notificationMessageRawJson fieldvar notificationMessageKeyValuesArray = vm.subscription()[0].notificationMessageKeyValues();
            var notificationMessageRawJson = String(vm.subscription()[0].notificationMessageRawJson());
            var notificationMessageRawJsonisInvalid = validateMessageRawJson(notificationMessageRawJson);
            if (notificationMessageRawJsonisInvalid) {
                error = true;
            }
        }

        var requirementsArray = vm.subscription()[0].requirements();
        for (i = 0; i < requirementsArray.length; i++) {
            var conditionsArray = requirementsArray[i].conditions();
            for (k = 0; k < conditionsArray.length; k++) {
                var conditionToTest = ko.toJSON(conditionsArray[k].jmespath());
                if (conditionToTest === '""') {
                    $('.emptyCondition').text("Condition must not be empty");
                    $('.emptyCondition').show();
                    error = true;
                }
            }
        }
        // If errors return.
        if (error) {
            $('#errorExists').text("Required fields not filled or invalid data");
            $('#errorExists').show();
            return false;
        }
        return true;
        //END: Check of other subscription fields values
    }

    function createParsedFormCopy(jsonCopiedData) {
        // Ensure MAIL notificationType has no restPostBodyMediaType
        if (jsonCopiedData[0].notificationType == "MAIL") {
            jsonCopiedData[0]['restPostBodyMediaType'] = "";
        }

        if (!vm.formpostkeyvaluepairs()) {
            // Since EI back end does not handle notificationMessageRawJson key we must inject that value to formvalue here
            // and ensures the list contains only one object.
            var notificationMessageKeyValues = [];
            var formKeyValuePair = { "formkey": "", "formvalue": jsonCopiedData[0].notificationMessageRawJson };
            notificationMessageKeyValues.push(formKeyValuePair);
            jsonCopiedData[0]['notificationMessageKeyValues'] = notificationMessageKeyValues;
        }

        // Delete notificationMessageRawJson since it is not used in back end.
        delete jsonCopiedData[0]['notificationMessageRawJson'];

        return jsonCopiedData
    }

    // /Start ## Save Subscription ##########################################
    $('div.modal-footer').on('click', 'button.save_record', function (event) {
        event.stopPropagation();
        event.preventDefault();

        var allSubscriptionFieldsIsValid = validateFormPostData();
        if (!allSubscriptionFieldsIsValid) {
            return
        }

        // Prepare copy of form data
        var jsonParsedFormData = ko.toJS(vm.subscription());
        var formDataToSend = createParsedFormCopy(jsonParsedFormData);

        var url = frontendServiceUrl + "/subscriptions";
        var type;
        if (save_method === 'add') {  // Add new
            type = "POST";

        } else if (save_method === 'edit') {  // Update existing
            type = "PUT";
        }

        console.log("Orginal = " + ko.toJSON(vm.subscription()));
        console.log("Copy = " + JSON.stringify(formDataToSend));

        // AJAX Callback handling
        var callback = {
            beforeSend: function () {
                $('#btnSave').text('Saving...'); //change button text
                $('#btnSave').attr('disabled', true); //set button disable
            },
            success: function (data, textStatus) {
                var returnData = [data];
                if (returnData.length > 0) {
                    $('#modal_form').modal('hide');
                    reload_table();
                    // Clear ObservableArray
                    vm.subscription([]);
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                var responseJSON = JSON.parse(XMLHttpRequest.responseText);
                var errors = "";
                for (var i = 0; i < responseJSON.length; i++) {
                    errors = errors + "\n" + responseJSON[i].reason;
                    $.jGrowl(responseJSON[i].subscription + " :: " + responseJSON[i].reason, { sticky: true, theme: 'Error' });
                }
                $('#serverError').text(errors);
                $('#serverError').show();
            },
            complete: function () {
                $('#btnSave').text('Save'); //change button text
                $('#btnSave').attr('disabled', false); //set button enable
            }
        };

        // Perform AJAX
        var ajaxHttpSender = new AjaxHttpSender();
        ajaxHttpSender.sendAjax(url, type, JSON.stringify(formDataToSend), callback);
    });
    // /Stop ## Save Subscription ###########################################

    // /Start ## Delete Subscription ########################################
    $('#table').on('click', 'tbody tr td button.delete_record', function (event) {
        event.stopPropagation();
        event.preventDefault();
        // Get tag that contains subscriptionName
        var id = $(this).attr("id").split("-")[1];
        var callback = {
            beforeSend: function () {
            },
            success: function (data, textStatus) {
                $.jGrowl('Subscription deleted!', {
                    sticky: false,
                    theme: 'Notify'
                });

                //if success reload ajax table
                $('#modal_form').modal('hide');
                reload_table();

            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                window.logMessages("Error: " + XMLHttpRequest.responseText);
            },
            complete: function () {
            }
        };

        $.confirm({
            title: 'Confirm!',
            content: 'Are you sure delete this subscription?',
            buttons: {
                confirm: function () {
                    var ajaxHttpSender = new AjaxHttpSender();
                    ajaxHttpSender.sendAjax(frontendServiceUrl + "/subscriptions/" + id, "DELETE", null, callback);
                },
                cancel: function () {
                }
            }
        });
    });
    // /Stop ## Delete Subscription #########################################

    // Delay display buttons
    setTimeout(showButtons, 800);
    function showButtons() {
        $(".loadingAnimation").hide();
        $(".subButtons").show();
    }

    function loadTooltip() {
        $('[data-toggle="tooltip"]').tooltip({ trigger: "click", html: true });
    }

    function closeTooltip() {
        $('.tooltip').tooltip('hide');
    }
});
