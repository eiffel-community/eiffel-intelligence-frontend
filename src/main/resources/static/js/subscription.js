// Global vars
var save_method;
var table;
var frontendServiceUrl;
var defaultFormKeyValuePair = { "formkey": "", "formvalue": "" };
var defaultFormKeyValuePairAuth = { "formkey": "Authorization", "formvalue": "" };

jQuery(document).ready(function () {

    $('.modal-dialog').draggable({ handle: ".modal-header", cursor: 'move' });
    $('[data-toggle="tooltip"]').tooltip();

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
            $("#userName").text(currentUser);
            $("#logoutBlock").show();
            $(".show_if_authorized").show();
        }
    }
    function doIfUserLoggedOut() {
        localStorage.removeItem("currentUser");
        $("#userName").text("Guest");
        $("#loginBlock").show();
        $("#logoutBlock").hide();
        $(".show_if_authorized").hide();
        localStorage.setItem('errorsStore', []);
    }

    // Check if EI Backend Server is online every X seconds
    window.setInterval(function () { checkBackendStatus(); }, 15000);

    // Check if EI Backend Server is online when Status Connection button is pressed.
    $('.container').on('click', 'button.btnEIConnectionStatus', function (event) {
        event.stopPropagation();
        event.preventDefault();

        checkBackendStatus();
    });
    // END OF EI Backend Server check #########################################


    // /Start ## Knockout ####################################################

    // Subscription model
    function subscription_model(data) {

        this.created = ko.observable(data.created);
        this.notificationMeta = ko.observable(data.notificationMeta);
        this.notificationType = ko.observable(data.notificationType);
        this.restPostBodyMediaType = ko.observable(data.restPostBodyMediaType);
        this.notificationMessageKeyValues = ko.observableArray(data.notificationMessageKeyValues);
        this.notificationMessageKeyValuesAuth = ko.observableArray(data.notificationMessageKeyValuesAuth);
        this.repeat = ko.observable(data.repeat);
        this.requirements = ko.observableArray(data.requirements);
        this.subscriptionName = ko.observable(data.subscriptionName);
        this.aggregationtype = ko.observable(data.aggregationtype);
        this.authenticationType = ko.observable(data.authenticationType);
        this.userName = ko.observable(data.userName);
        this.token = ko.observable(data.token);

        this.notificationType.subscribe(function (new_value) {
            vm.subscription()[0].restPostBodyMediaType(null);
            vm.formpostkeyvaluepairs(false);

        });

        this.restPostBodyMediaType.subscribe(function (new_value) {
            if (new_value == "application/x-www-form-urlencoded") {
                vm.formpostkeyvaluepairs(true);
            } else {
                vm.formpostkeyvaluepairs(false);
            }
        });
    }

    function formdata_model(formdata) {
        this.formkey = ko.observable(formdata.formkey);
        this.formvalue = ko.observable(formdata.formvalue);
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
        self.subscription_templates_in = ko.observableArray(
            [
                { "text": "Jenkins Pipeline Parameterized Job Trigger", value: "templatejenkinsPipelineParameterizedBuildTrigger" },
                { "text": "REST POST (Raw Body : JSON)", value: "templateRestPostJsonRAWBodyTrigger" },
                { "text": "Mail Trigger", value: "templateEmailTrigger" }
            ]);
        self.choosen_subscription_template = ko.observable();
        self.authenticationType = ko.observable();
        self.formpostkeyvaluepairs = ko.observable(false);
        self.formpostkeyvaluepairsAuth = ko.observable(false);
        self.notificationType_in = ko.observableArray(
            [
                { "text": "REST_POST", value: "REST_POST" },
                { "text": "MAIL", value: "MAIL" }
            ]);
        self.authenticationType_in = ko.observableArray(
            [
                { "text": "NO_AUTH", value: "NO_AUTH" },
                { "text": "BASIC_AUTH", value: "BASIC_AUTH" }
            ]);


        self.restPostBodyType_in = ko.observableArray(
            [
                { "text": "FORM/POST Parameters (application/x-www-form-urlencoded)", value: "application/x-www-form-urlencoded" },
                { "text": "RAW BODY: JSON (application/json)", value: "application/json" }
            ]);

        self.repeat_in = ko.observableArray([true, false]);

        self.add_requirement = function (data, event) {

            var conditions_array = [];
            conditions_array.push(new jmespath_model({ "jmespath": ko.observable("") }));
            self.subscription()[0].requirements().push(new conditions_model(conditions_array));

            // Force update
            var data = self.subscription().slice(0);
            self.subscription([]);
            self.subscription(data);

            self.subscription.valueHasMutated();

        };


        self.choosen_subscription_template.subscribe(function (template_var) {
            if (self.choosen_subscription_template() != null) { // only execute if value exists
                json_obj_clone = JSON.parse(JSON.stringify(template_vars[template_var]));
                populate_json(json_obj_clone, "add");
            }
        });


        self.addNotificationMsgKeyValuePair = function (data, event) {
            self.subscription()[0].notificationMessageKeyValues.push(new formdata_model(defaultFormKeyValuePair));

            // Force update
            var data = self.subscription().slice(0);
            self.subscription([]);
            self.subscription(data);
            self.subscription.valueHasMutated();
        };

        self.addNotificationMsgKeyValuePairAuth = function (data, event) {
            data.notificationMessageKeyValues.push({
                "formkey": "Authorization", "formvalue": ko.computed(function () {
                    return "Basic " + btoa(data.userName() + ":" + data.token());

                })
            });
            //        	   ko.observable(value);
            // Force update
            var data = self.subscription().slice(0);
            self.subscription([]);
            self.subscription(data);
            self.subscription.valueHasMutated();
        };


        self.getUTCDate = function (epochtime) {
            var d = new Date(0); // The 0 there is the key, which sets the date to the epoch
            d.setUTCMilliseconds(epochtime);
            return d;  // Is now a date (in client time zone)
        }


        self.add_condition = function (data, event, requirement_index) {
            self.subscription()[0].requirements()[ko.toJSON(requirement_index)].conditions().push(new jmespath_model({ "jmespath": ko.observable("") }));
            // Force update
            var data = self.subscription().slice(0);
            self.subscription([]);
            self.subscription(data);
            self.subscription.valueHasMutated();
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
                "title": "UserName",
                "data": "userName",
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
                "title": "Type",
                "data": "aggregationtype"
            },
            {
                "targets": [5],
                "orderable": true,
                "title": "Date",
                "data": "created",
                "mRender": function (data, type, row, meta) {
                    return vm.getUTCDate(data);
                }
            },
            {
                "targets": [6],
                "orderable": true,
                "title": "NotificationType",
                "data": "notificationType"
            },
            {
                "targets": [7],
                "orderable": true,
                "title": "NotificationMeta",
                "data": "notificationMeta"
            },
            {
                "targets": [8],
                "orderable": true,
                "title": "Repeat",
                "data": "repeat"
            },
            {
                "targets": [9], //last column
                "className": "sub-action-column",
                "orderable": false,
                "title": "Action",
                "data": null,
                "render": function (data, type, row, meta) {
                    if (isSecured == true && row.userName == currentUser && row.userName != null) {
                        return '<button id="view-' + data.subscriptionName + '" data-toggle="tooltip" title="View subscription" class="btn btn-sm btn-success view_record">View</button> '
                            + '<button id="edit-' + data.subscriptionName + '" data-toggle="tooltip" title="Edit subscription" class="btn btn-sm btn-primary edit_record">Edit</button> '
                            + '<button id="delete-' + data.subscriptionName + '" data-toggle="tooltip" title="Delete subscription from EI" class="btn btn-sm btn-danger delete_record">Delete</button>';
                    } else if (isSecured == false) {
                        return '<button id="view-' + data.subscriptionName + '" data-toggle="tooltip" title="View subscription" class="btn btn-sm btn-success view_record">View</button> '
                            + '<button id="edit-' + data.subscriptionName + '" data-toggle="tooltip" title="Edit subscription" class="btn btn-sm btn-primary edit_record">Edit</button> '
                            + '<button id="delete-' + data.subscriptionName + '" data-toggle="tooltip" title="Delete subscription from EI" class="btn btn-sm btn-danger delete_record">Delete</button>';
                    } else {
                        return '<button id="view-' + data.subscriptionName + '" data-toggle="tooltip" title="View subscription" class="btn btn-sm btn-success view_record">View</button>';
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


    // /Start ## Reload Table################################################
    $('.container').on('click', 'button.table_reload', function (event) {
        reload_table();
    });
    // /Stop ## Reload Table#################################################


    // /Start ## Bulk delete#################################################
    $('.container').on('click', 'button.bulk_delete', function (event) {
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
    $('.container').on('click', 'button.get_subscription_template', function (event) {
        event.stopPropagation();
        event.preventDefault();
        getTemplate();
    });
    // /END ## get_subscription_template #################################################



    function validateJsonAndCreateSubscriptions(subscriptionFile) {
        var reader = new FileReader();
        reader.onload = function () {
            var fileContent = reader.result;
            var jsonLintResult = "";
            try {
                jsonLintResult = jsonlint.parse(fileContent);
            } catch (e) {
                window.logMessages("JSON Format Check Failed:\n" + e.name + "\n" + e.message);
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
    $('.container').on('click', 'button.upload_subscriptions', function (event) {
        event.stopPropagation();
        event.preventDefault();

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


    // /Start ## Add Subscription ########################################
    $('.container').on('click', 'button.btn.btn-success.add_subscription', function (event) {
        event.stopPropagation();
        event.preventDefault();
        vm.choosen_subscription_template(null);
        json_obj_clone = JSON.parse(JSON.stringify(default_json_empty));
        populate_json(json_obj_clone, "add");
    });
    // /Stop ## Add Subscription ############################################


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
                for (i = 0; i < item[0].notificationMessageKeyValues.length; i++) {
                    item[0].notificationMessageKeyValues[i] = new formdata_model(item[0].notificationMessageKeyValues[i])
                }


                return new subscription_model(item[0]);
            });
            // Load data into observable array
            vm.subscription(mappedPackageInfo);
            // Force update
            vm.subscription()[0].restPostBodyMediaType.valueHasMutated();
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
    }
    // /Stop ## pupulate JSON  ###########################################


    // /Start ## Save Subscription ##########################################
    $('div.modal-footer').on('click', 'button.save_record', function (event) {
        var error = false;
        event.stopPropagation();
        event.preventDefault();
        var notificationMessageKeyValuesArray = vm.subscription()[0].notificationMessageKeyValues();
        if (!vm.formpostkeyvaluepairs()) {
            notificationMessageKeyValuesArray[0].formkey = ""; // OBS must be empty when NOT using REST POST Form key/value pairs
        }

        $('.hiddenField').hide();
        //START: Make sure all datatables field has a value
        // Validate SubscriptionName field
        if (vm.subscription()[0].subscriptionName() == "") {
            window.logMessages("Error: SubscriptionName field must have a value");
            $('#noNameGiven').show();
            error = true;
        }
        if (!(/[a-z]|[A-Z]|[0-9]|[\_]/.test(String(vm.subscription()[0].subscriptionName()).slice(-1)))) {
            window.logMessages("Only numbers,letters and underscore is valid to type in subscriptionName field.");
            $('#invalidLetters').show();
            error = true;
        }

        // Validate notificationType field
        if (vm.subscription()[0].notificationType() == null) {
            window.logMessages("Error: notificationType field must boolean a value");
            $('#notificationTypeNotSet').show();
            error = true;
        }
        // Validate notificationMeta field
        if (vm.subscription()[0].notificationMeta() == "") {
            window.logMessages("Error: notificationMeta field must have a value");
            $('#noNotificationMetaGiven').show();
            error = true;
        }
        // Validate repeat field
        if (vm.subscription()[0].repeat() == null) {
            window.logMessages("Error: repeat field must have a boolean value");
            $('#repeatNotSet').show();
            error = true;
        }
        //END OF: Make sure all datatables field has a value

        //START: Check of other subscription fields values
        for (i = 0; i < notificationMessageKeyValuesArray.length; i++) {
            var test_key = ko.toJSON(notificationMessageKeyValuesArray[i].formkey);
            var test_value = ko.toJSON(notificationMessageKeyValuesArray[i].formvalue());
            if (vm.formpostkeyvaluepairs()) {
                if (test_key.replace(/\s/g, "") === '""' || test_value.replace(/\s/g, "") === '""') {
                    window.logMessages("Error: Value & Key  in notificationMessage must have a values!");
                    $('#noNotificationKeyOrValue').show();
                    error = true;
                }
            }
            else {
                if (notificationMessageKeyValuesArray.length !== 1) {
                    window.logMessages("Error: Only one array is allowed for notificationMessage when NOT using key/value pairs!");
                    $('#notificationMessageKeyValuesArrayToLarge').show();
                    error = true;
                }
                else if (test_key !== '""') {
                    window.logMessages("Error: Key in notificationMessage must be empty when NOT using key/value pairs!");
                    $('#keyInNotificationMessage').show();
                    error = true;
                }
                else if (test_value.replace(/\s/g, "") === '""') {
                    window.logMessages("Error: Value in notificationMessage must have a value when NOT using key/value pairs!");
                    $('#noNotificationMessage').show();
                    error = true;
                }
            }
        }

        var requirementsArray = vm.subscription()[0].requirements();
        for (i = 0; i < requirementsArray.length; i++) {
            var conditionsArray = requirementsArray[i].conditions();
            for (k = 0; k < conditionsArray.length; k++) {
                var test_me = ko.toJSON(conditionsArray[k].jmespath());
                if (test_me === '""') {
                    window.logMessages("Error: JMESPath field must have a value");
                    $('#emptyCondition1').show();
                    $('.emptyCondition').show();
                    error = true;
                }
            }
        }
        // If errors return.
        if (error) {
            return;
        }
        //END: Check of other subscription fields values

        var id = ko.toJSON(vm.subscription()[0].subscriptionName).trim();

        var url;
        var type;
        if (save_method === 'add') {  // Add new
            url = frontendServiceUrl + "/subscriptions";
            type = "POST";

        } else if (save_method === 'edit') {  // Update existing
            url = frontendServiceUrl + "/subscriptions";
            type = "PUT";
        }


        // AJAX Callback handling
        var callback = {
            beforeSend: function () {
                $('#btnSave').text('saving...'); //change button text
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
                for (var i = 0; i < responseJSON.length; i++) {
                    $.jGrowl(responseJSON[i].subscription + " :: " + responseJSON[i].reason, { sticky: true, theme: 'Error' });
                }
            },
            complete: function () {
                $('#btnSave').text('save'); //change button text
                $('#btnSave').attr('disabled', false); //set button enable
            }
        };


        // Perform AJAX
        var ajaxHttpSender = new AjaxHttpSender();
        ajaxHttpSender.sendAjax(url, type, ko.toJSON(vm.subscription()), callback);



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
});