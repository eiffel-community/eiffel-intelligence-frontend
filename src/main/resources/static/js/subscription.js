// Global vars
var save_method;
var table;
var frontendServiceUrl;
var subscriptionTemplateFile;


jQuery(document).ready(function() {


    /*
    $('.modal-content').resizable({
        //minHeight: 300,
        //minWidth: 300
    });
    $('#modal_form').on('show.bs.modal', function () {
        $(this).find('.modal-body').css({
            'max-height':'100%'
        });
    });
    */

    $('.modal-dialog').draggable({ handle: ".modal-header", cursor: 'move' });





    // Fetch injected URL from DOM
    frontendServiceUrl = $('#frontendServiceUrl').text();

    // /Start ## Global AJAX Sender function ##################################
    var AjaxHttpSender = function () {};

    AjaxHttpSender.prototype.sendAjax = function (url, type, data, callback) {
        $.ajax({
            url : url,
            type : type,
            data : data,
            contentType : 'application/json; charset=utf-8',
            dataType : "json",
            cache: false,
            beforeSend : function () {
                callback.beforeSend();
            },
            error : function (XMLHttpRequest, textStatus, errorThrown) {
                callback.error(XMLHttpRequest, textStatus, errorThrown);
            },
            success : function (data, textStatus) {
                callback.success(data, textStatus);
            },
            complete : function (XMLHttpRequest, textStatus) {
                callback.complete();
            }
        });
    }
    // /Stop ## Global AJAX Sender function ##################################

    var checkEiBackend = false;
    // Check EI Backend Server Status ########################################
    function checkEiBackendServer() {
    	var EIConnBtn = document.getElementById("btnEIConnection");
    	if (EIConnBtn == null ) {
    		return;
    	}
    	   $.ajax({
    		      url: frontendServiceUrl + "/subscriptions/testDummySubscription",
    		      contentType : 'application/json; charset=utf-8',
    		      type: 'GET',
                  error : function (XMLHttpRequest, textStatus, errorThrown) {
                  		var red="#ff0000";
                  		EIConnBtn.style.background = red;
                  },
                  success : function (data, textStatus, xhr) {
                  		var green="#00ff00";
                  		EIConnBtn.style.background = green;
                  		checkEiBackend=true;
                  },
    		      complete: function (XMLHttpRequest, textStatus) {
    		      }
    		   });

    }

    // Check if EI Backend Server is online every X seconds
    window.setInterval(function(){
    	checkEiBackendServer();
    }, 15000);
    
    // Check if EI Backend Server is online when Status Connection button is pressed.
    $('.container').on( 'click', 'button.btnEIConnectionStatus', function (event) {

        event.stopPropagation();
        event.preventDefault();

        checkEiBackendServer();
    });

    // END OF EI Backend Server check ######################################### 
    
    
    // /Start ## Knockout ####################################################

    // Subscription model
    function subscription_model(data){
    	
        this.created = ko.observable(data.created);
        this.notificationMessage = ko.observable(data.notificationMessage);
        this.notificationMeta = ko.observable(data.notificationMeta);
        this.notificationType = ko.observable(data.notificationType);
        this.repeat = ko.observable(data.repeat);
        this.requirements = ko.observableArray(data.requirements);
        this.subscriptionName = ko.observable(data.subscriptionName);
        this.aggregationtype = ko.observable(data.aggregationtype);


        
        // Validating subscriptionName inputs
        this.subscriptionName.subscribe(function (subscriptionName) {
        	if (!(/[a-z]|[A-Z]|[0-9]|[\_]/.test(String(subscriptionName).slice(-1)))) {
                $.jGrowl("Only numbers,letters and underscore is valid to type in subscriptionName field.", {
                    sticky : false,
                    theme : 'Error'
                });
        	}
        });
        
        // Validating notificationMeta inputs
        this.notificationMeta.subscribe(function (notificationMeta) {
        	if (!(/[a-z]|[A-Z]|[0-9]|[\:\/\.]/.test(String(notificationMeta).slice(-1)))) {
                $.jGrowl("Only numbers and letters is valid to type in notificationMeta field.", {
                    sticky : false,
                    theme : 'Error'
                });
        	}
        });
    }



    function conditions_model(condition){
        this.conditions = ko.observableArray(condition);
    }

    function jmespath_model(jmespath){
        this.jmespath = ko.observable(jmespath.jmespath);
    }


     // ViewModel - SubscriptionViewModel
    var SubscriptionViewModel = function(){
        var self = this;
        self.subscription  = ko.observableArray([]);
        self.notificationType_in  = ko.observableArray(
        		[
        			{"text": "REST_POST", value:"REST_POST"},
                    {"text": "REST_POST_JENKINS", value:"REST_POST_JENKINS"},
        			{"text": "MAIL", value:"MAIL"}
        		]);

        self.repeat_in  = ko.observableArray([true, false]);


        self.add_requirement = function(data, event) {

            var conditions_array = [];
            conditions_array.push(new jmespath_model({"jmespath": ko.observable("")}));
            self.subscription()[0].requirements().push(new conditions_model(conditions_array));

            // Force update
            var data = self.subscription().slice(0);
            self.subscription([]);
            self.subscription(data);

            self.subscription.valueHasMutated();

        };



        self.getUTCDate= function(epochtime){
            var d = new Date(0); // The 0 there is the key, which sets the date to the epoch
            d.setUTCMilliseconds(epochtime);
            return d;  // Is now a date (in client time zone)
        }


        self.add_condition = function(data, event, requirement_index) {

            self.subscription()[0].requirements()[ko.toJSON(requirement_index)].conditions().push(new jmespath_model({"jmespath": ko.observable("")}));

            // Force update
            var data = self.subscription().slice(0);
            self.subscription([]);
            self.subscription(data);

            self.subscription.valueHasMutated();
        };


        self.delete_condition = function (data, event, requirement_item , condition_index, requirement_index) {

            self.subscription()[0].requirements()[ko.toJSON(requirement_index)].conditions.remove(data);
            if(self.subscription()[0].requirements()[ko.toJSON(requirement_index)].conditions().length <= 0)
            {
                self.subscription()[0].requirements.remove(self.subscription()[0].requirements()[ko.toJSON(requirement_index)]);
            }

        };




    };// var SubscriptionViewModel = function(){

	// Cleanup old ViewModel and Knockout Obeservables from previous page load.
    var observableObject = $('#ViewModelDOMObject')[0]; 
    ko.cleanNode(observableObject);
    // Apply bindings
	var vm = new SubscriptionViewModel();
    ko.applyBindings(vm,  document.getElementById("ViewModelDOMObject"));


    // /Stop ## Knockout #####################################################





    // /Start ## Datatables ##################################################
    table = $('#table').DataTable({

        "processing": true, //Feature control the processing indicator.
        "serverSide": false, //Feature control DataTables' server-side processing mode.
        "fixedHeader": true,
        "order": [], //Initial no order.

        // Load data for the table's content from an Ajax source
        "ajax": {
            "url": frontendServiceUrl + "/subscriptions",
            "type": "GET",
            "dataSrc": ""   // Flat structure from EI backend REST API

        },


        //Set column definition initialisation properties.
        "columnDefs": [
            {
                "targets": [ 0 ],
                "orderable": false,
                "data": "subscriptionName",
                "title": '<input type="checkbox" id="check-all" />',
                "render": function ( data, type, row, meta ) {
                    return '<input type="checkbox" class="data-check" value="'  + data + '">';
                }
            },
            {
                "targets": [ 1 ],
                "orderable": true,
                "title": "SubscriptionName",
                "data": "subscriptionName"
            },
            {
                "targets": [ 2 ],
                "orderable": true,
                "title": "Type",
                "data": "aggregationtype"
            },
            {
                "targets": [ 3 ],
                "orderable": true,
                "title": "Date",
                "data": "created",
                "mRender" : function (data, type, row, meta) {
                    return vm.getUTCDate(data);
                }
            },
            {
                "targets": [ 4 ],
                "orderable": true,
                "title": "NotificationType",
                "data": "notificationType"
            },
            {
                "targets": [ 5 ],
                "orderable": true,
                "title": "NotificationMeta",
                "data": "notificationMeta"
            },
            {
                "targets": [ 6 ],
                "orderable": true,
                "title": "Repeat",
                "data": "repeat"
            },
            {
                "targets": [ 7 ], //last column
                "orderable": false,
                "title": "Action",
                "data": null,
                "width":"150px",
                "defaultContent": '<button data-toggle="tooltip" title="Edit subscription" class="btn btn-sm btn-primary edit_record">Edit</button><button data-toggle="tooltip" title="Delete subscription from EI" class="btn btn-sm btn-danger delete_record">Delete</button>'


            },

        ],
      
    });
    // /Stop ## Datatables ##################################################


    // /Start ## check all subscriptions ####################################
    $("#check-all").click(function () {
        $(".data-check").prop('checked', $(this).prop('checked'));
    });
    // /Stop ## check all subscriptions #####################################


    // /Start ## Reload Table################################################
    $('.container').on( 'click', 'button.table_reload', function (event) {
        reload_table();
    });
    // /Stop ## Reload Table#################################################

    
    // /Start ## Bulk delete#################################################
    $('.container').on( 'click', 'button.bulk_delete', function (event) {
    	var subScriptionsToDelete = [];
    	var data = table.rows().nodes();
    	$.each(data, function (index, value) {
    		if ($(this).find('input').prop('checked') == true){
//    			console.log("Index: " + index + " Checked: " + $(this).find('input').prop('checked') + " SubscriptionName: " + table.row(index).data().subscriptionName)
    			subScriptionsToDelete.push(table.row(index).data().subscriptionName)
    	    }
    	});
    	
    	// Check if no Subscription has been marked to be deleted.
    	if ( subScriptionsToDelete.length < 1 ){
    		$.alert("No subscriptions has been marked to be deleted.");
    		return;
    	}
    	
    	var subScriptionsToDeleteString = "";
    	for (i=0; i < subScriptionsToDelete.length; i++) {
    		subScriptionsToDeleteString += subScriptionsToDelete[i] + "\n";
    	}
    	//alert('Do you really want to delete these subscriptions:\n' + subScriptionsToDeleteString);
        
    	var callback = {
                beforeSend : function () {
                },
                success : function (data, textStatus) {
                    $.jGrowl('Subscriptions deleted!', {
                        sticky : false,
                        theme : 'Notify'
                    });

                    //if success reload ajax table
                    $('#modal_form').modal('hide');
                    reload_table();

                },
                error : function (XMLHttpRequest, textStatus, errorThrown) {
                    $.jGrowl("Error: " + XMLHttpRequest.responseText, {
                        sticky : true,
                        theme : 'Error'
                    });
                },
                complete : function () {
                }
            };


             $.confirm({
                 title: 'Confirm!',
                 content: 'Are you sure you want to delete these subscriptions?<pre>' + subScriptionsToDeleteString,
                 buttons: {
                     confirm: function () {
                    	 var ajaxHttpSender = new AjaxHttpSender();
                    	 for (i=0; i < subScriptionsToDelete.length; i++){
                    		 ajaxHttpSender.sendAjax(frontendServiceUrl + "/subscriptions/"+subScriptionsToDelete[i], "DELETE", null, callback);
                    	 }
                     },
                     cancel: function () {
                     }
                 }
             });

    });
    // /Stop ## Bulk delete##################################################

    
    // /Start ## get_subscription_template #################################################
    $('.container').on( 'click', 'button.get_subscription_template', function (event) {
    	event.stopPropagation();
        event.preventDefault();
        function getTemplate(){
            var req = new XMLHttpRequest();
            req.open("GET", '/download/subscriptiontemplate', true);
            req.responseType = "blob";
            req.onload = function (event) {
                var blob = req.response;
                var link = document.createElement('a');
                link.href = window.URL.createObjectURL(blob);
                link.download = 'subscriptionsTemplate.json';
                link.click();
            };
            req.send();}
        getTemplate();
    });
    // /END ## get_subscription_template #################################################


    // /Start ## upload_subscriptions #################################################
    $('.container').on( 'click', 'button.upload_subscriptions', function (event) {
    
    	event.stopPropagation();
        event.preventDefault();
        
        function tryToCreateSubscription(subscriptionJson) {
        	// Send Subscription JSON file to Spring MVC
            // AJAX Callback handling
            var callback = {
                beforeSend : function () {
                },
                success : function (data, textStatus) {

                    var returnData = [data];
                    if (returnData.length > 0) {
                        $.jGrowl("Successful created subscription " + subscriptionJson.subscriptionName, {
                            sticky : false,
                            theme : 'Error'
                        });
                        reload_table();
                    }

                },
                error : function (XMLHttpRequest, textStatus, errorThrown) {

                    $.jGrowl("Failed to create Subscription: " + subscriptionJson.subscriptionName + " Error: " + XMLHttpRequest.responseText, {
                        sticky : false,
                        theme : 'Error'
                    });

                },
                complete : function () {
                }
            };

            // Perform AJAX
            var ajaxHttpSender = new AjaxHttpSender();
            ajaxHttpSender.sendAjax(frontendServiceUrl + "/subscriptions", "POST", ko.toJSON(subscriptionJson), callback);
        }
        
        function validateJsonAndCreateSubscriptions(subscriptionFile){
        	
            var reader = new FileReader();
            
            reader.onload = function() {
            var fileContent = reader.result;
            
            var jsonLintResult="";
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
            
            var subscriptionJsonList = JSON.parse(fileContent);
                tryToCreateSubscription(subscriptionJsonList);
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
    $('.container').on( 'click', 'button.btn.btn-success.add_subscription', function (event) {

        event.stopPropagation();
        event.preventDefault();


        json_obj_clone = JSON.parse(JSON.stringify(default_json_empty));

        populate_json(json_obj_clone, "add");


    });
    // /Stop ## Add Subscription ############################################


    // /Start ## Reload Datatables ###########################################
    function reload_table()
    {
        table.ajax.reload(null,false); //reload datatable ajax
    }
    // /Stop ## Reload Datatables ############################################


    // /Start ## Edit Subscription ###########################################
    $('#table').on( 'click', 'tbody tr td button.edit_record', function (event) {

        event.stopPropagation();
        event.preventDefault();

        // Fetch datatable row -> subscriptionName
        var datatable_row_data = table.row( $(this).parents('tr') ).data();
        var id = datatable_row_data.subscriptionName;

        // AJAX Callback handling
        var callback = {
            beforeSend : function () {
            },
            success : function (data, textStatus) {

                populate_json(data, "edit");

            },
            error : function (XMLHttpRequest, textStatus, errorThrown) {
                $.jGrowl("Error: " + XMLHttpRequest.responseText, {
                    sticky : true,
                    theme : 'Error'
                });
            },
            complete : function () {
            }
        };

        // Perform AJAX
        var ajaxHttpSender = new AjaxHttpSender();
        ajaxHttpSender.sendAjax(frontendServiceUrl + "/subscriptions/"+id, "GET", null, callback);

    });
    // /Stop ## Edit Subscription ###########################################


   // /Start ## populate JSON  ###########################################
    function populate_json(data, save_method_in)
    {
        var returnData = [data];
        if (returnData.length > 0) {


            vm.subscription([]);

            // Map JSON to Model and observableArray
            var mappedPackageInfo = $.map(returnData, function (item) {

                // Defining Observable on all parameters in Requirements array(which is defined as ObservableArray)

                for (i=0; i < item[0].requirements.length; i++) {

                    var conditions_array = [];

                    for (k = 0; k < item[0].requirements[i].conditions.length; k++) {

                        var jmespath_temp = item[0].requirements[i].conditions[k].jmespath;

                        conditions_array.push(new jmespath_model({"jmespath": ko.observable(jmespath_temp)}));

                    }

                    item[0].requirements[i] = new conditions_model(conditions_array);

                }
                return new subscription_model(item[0]);


            });

            // Load data into observable array
            vm.subscription(mappedPackageInfo);


            $('#modal_form').modal('show');
            
			if(data === "edit")				
			{
				title_ = 'Subscription: ' + data.subscriptionName
				
			}else
			{
				title_ = 'Add Subscription';
			}
			
			$('.modal-title').text(title_);

            save_method = save_method_in;
        }

    }
   // /Stop ## pupulate JSON  ###########################################


    // /Start ## Save Subscription ##########################################
    $('div.modal-footer').on( 'click', 'button.save_record', function (event) {

        event.stopPropagation();
        event.preventDefault();

        //START: Make sure all datatables field has a value
        if (vm.subscription()[0].subscriptionName() == "") {
            $.jGrowl("Error: SubscriptionName field must have a value", {
                sticky : true,
                theme : 'Error'
            });
            return;
        }
        if (vm.subscription()[0].notificationType() == null) {
            $.jGrowl("Error: notificationType field must boolean a value", {
                sticky : true,
                theme : 'Error'
            });
            return;
        }
        if (vm.subscription()[0].notificationMeta() == "") {
            $.jGrowl("Error: notificationMeta field must have a value", {
                sticky : true,
                theme : 'Error'
            });
            return;
        }
        if (vm.subscription()[0].repeat() == null) {
            $.jGrowl("Error: repeat field must have a boolean value", {
                sticky : true,
                theme : 'Error'
            });
            return;
        }
        //END OF: Make sure all datatables field has a value

        //START: Check of other subscription fields values
        if (vm.subscription()[0].notificationMessage() == "") {
            $.jGrowl("Error: notificationMessage field must have a value", {
                sticky : true,
                theme : 'Error'
            });
            return;
        }


        var requirementsArray = vm.subscription()[0].requirements();
        for (i=0; i < requirementsArray.length; i++){
            var conditionsArray = requirementsArray[i].conditions();
            for (k=0; k < conditionsArray.length; k++) {
                var test_me = ko.toJSON(conditionsArray[k].jmespath());
                if (test_me === '""') {
                    $.jGrowl("Error: jmepath field must have a value", {
                        sticky: true,
                        theme: 'Error'
                    });
                    return;
                }
            }
        }
        //END: Check of other subscription fields values

        var id = ko.toJSON(vm.subscription()[0].subscriptionName).trim();

        var url;
        var type;
        if(save_method === 'add') {  // Add new
            url = frontendServiceUrl + "/subscriptions";
            type = "POST";

        } else {  // Update existing
            url = frontendServiceUrl + "/subscriptions";
            type = "PUT";
        }


        // AJAX Callback handling
        var callback = {
            beforeSend : function () {
                $('#btnSave').text('saving...'); //change button text
                $('#btnSave').attr('disabled',true); //set button disable
            },
            success : function (data, textStatus) {

                var returnData = [data];
                if (returnData.length > 0) {

                    $('#modal_form').modal('hide');
                    reload_table();

                    // Clear ObservableArray
                    vm.subscription([]);

                }

            },
            error : function (XMLHttpRequest, textStatus, errorThrown) {

                $.jGrowl("Error: " + XMLHttpRequest.responseText, {
                    sticky : true,
                    theme : 'Error'
                });

            },
            complete : function () {
                $('#btnSave').text('save'); //change button text
                $('#btnSave').attr('disabled',false); //set button enable
            }
        };


        // Perform AJAX
        var ajaxHttpSender = new AjaxHttpSender();
        ajaxHttpSender.sendAjax(url, type, ko.toJSON(vm.subscription()), callback);



    });
    // /Stop ## Save Subscription ###########################################




    // /Start ## Delete Subscription ########################################
    $('#table').on( 'click', 'tbody tr td button.delete_record', function (event) {

        event.stopPropagation();
        event.preventDefault();

        var datatable_row_data = table.row( $(this).parents('tr') ).data();
        var id = datatable_row_data.subscriptionName.trim();


        var callback = {
            beforeSend : function () {
            },
            success : function (data, textStatus) {
                $.jGrowl('Subscription deleted!', {
                    sticky : false,
                    theme : 'Notify'
                });

                //if success reload ajax table
                $('#modal_form').modal('hide');
                reload_table();

            },
            error : function (XMLHttpRequest, textStatus, errorThrown) {
                $.jGrowl("Error: " + XMLHttpRequest.responseText, {
                    sticky : true,
                    theme : 'Error'
                });
            },
            complete : function () {
            }
        };


         $.confirm({
             title: 'Confirm!',
             content: 'Are you sure delete this subscription?',
             buttons: {
                 confirm: function () {
                     var ajaxHttpSender = new AjaxHttpSender();
                     ajaxHttpSender.sendAjax(frontendServiceUrl + "/subscriptions/"+id, "DELETE", null, callback);
                 },
                 cancel: function () {
                 }
             }
         });

    });
    // /Stop ## Delete Subscription #########################################




});  // $(document).ready(function() {

