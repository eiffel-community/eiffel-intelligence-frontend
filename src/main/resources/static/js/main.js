// Global vars
var save_method;
var table;
var backendServiceUrl;
var subscriptionTemplateFile;

$(document).ready(function() {


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
    backendServiceUrl = $('#backendServiceUrl').text();

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

    
    // Check EI Backend Server Status ########################################
    function checkEiBackendServer() {
    	var EIConnBtn = document.getElementById("btnEIConnection");

    	   $.ajax({
    		      url: backendServiceUrl + "/subscriptions/testDummySubscription",
    		      contentType : 'application/json; charset=utf-8',
    		      type: 'GET',
                  error : function (XMLHttpRequest, textStatus, errorThrown) {
//                  	console.log("EI BACKEND OFFLINE");
                  		var red="#ff0000";
                  		EIConnBtn.style.background = red;
                  },
                  success : function (data, textStatus, xhr) {
//                	  	console.log("DATA: " + xhr.responseText);
//                  	console.log("EI BACKEND ONLINE");
                  		var green="#00ff00";
                  		EIConnBtn.style.background = green;
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
        	if (!(/[a-z]|[A-Z]|[0-9]/.test(String(subscriptionName).slice(-1)))) {
                $.jGrowl("Only numbers and letters is valid to type in subscriptionName field.", {
                    sticky : false,
                    theme : 'Error'
                });
        	}
        });
        
        // Validating notificationMeta inputs
        this.notificationMeta.subscribe(function (notificationMeta) {
        	if (!(/[a-z]|[A-Z]|[0-9]/.test(String(notificationMeta).slice(-1)))) {
                $.jGrowl("Only numbers and letters is valid to type in notificationMeta field.", {
                    sticky : false,
                    theme : 'Error'
                });
        	}
        });
    }
    

    function conditions_model(condition){
        this.jmepath = ko.observable(condition.conditions.jmepath);
    }


     // ViewModel - SubscriptionViewModel
    var SubscriptionViewModel = function(){
        var self = this;
        self.subscription  = ko.observableArray([]);
        self.notificationType_in  = ko.observableArray(
        		[
        			{"text": "REST_POST", value:"REST_POST"},
        			{"text": "MAIL", value:"MAIL"}
        		]);

        self.repeat_in  = ko.observableArray([true, false]);

    };

    // Apply bindings
    var vm = new SubscriptionViewModel();
    ko.applyBindings(vm);



    // /Stop ## Knockout #####################################################




    // /Start ## Datatables ##################################################
    table = $('#table').DataTable({

        "processing": true, //Feature control the processing indicator.
        "serverSide": false, //Feature control DataTables' server-side processing mode.
        "fixedHeader": true,
        "order": [], //Initial no order.

        // Load data for the table's content from an Ajax source
        "ajax": {
            "url": backendServiceUrl + "/subscriptions",
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
                "title": "Created",
                "data": "created"
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
                    		 ajaxHttpSender.sendAjax(backendServiceUrl + "/subscriptions/"+subScriptionsToDelete[i], "DELETE", null, callback);
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
        
        function createDownloadWindow(filename, url) {
            var pom = document.createElement('a');
            pom.setAttribute('href', url);
            pom.setAttribute('download', filename);

            if (document.createEvent) {
                var event = document.createEvent('MouseEvents');
                event.initEvent('click', true, true);
                pom.dispatchEvent(event);
            }
            else {
                pom.click();
            }
        }
                
        createDownloadWindow("SubscriptionsTemplate.json","\/download\/subscriptiontemplate");
    	
    });
    // /END ## get_subscription_template #################################################


    // /Start ## upload_subscriptions #################################################
    $('.container').on( 'click', 'button.upload_subscriptions', function (event) {
    
    	event.stopPropagation();
        event.preventDefault();
        
        function validateSubscriptionFormat(subscriptionFile) {
        	// Send Subscription JSON file to Spring MVC
        	
        }
        
        function validateJsonandSubscriptionFormat(subscriptionFile){
        	
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
//              console.log("JsonLint Result: " + jsonLintResult.toSource());
            $.jGrowl('JSON Format Check Succeeded', {
                sticky : false,
                theme : 'Notify'
            });  
            
              validateSubscriptionFormat(subscriptionFile);
            };
            
            reader.readAsText(subscriptionFile);
        	
        }
        
        function createUploadWindow() {
        	
        	
            var pom = document.createElement('input');
            pom.setAttribute('id', 'uploadFile');
            pom.setAttribute('type', 'file');
            pom.setAttribute('name', 'upFile');
          
            // Internet Explorer adaptation
            pom.setAttribute('onchange', "function uploadFinished() {" +
            		"var subscriptionFile = pom.files[0];" +
            		"console.log('Uploaded File: ' + subscriptionFile);" +
            		"validateJsonandSubscriptionFormat(subscriptionFile);" +
            		"};");
          
            // All other Web Browser
            pom.onchange = function uploadFinished() {
            	var subscriptionFile = pom.files[0];
        		validateJsonandSubscriptionFormat(subscriptionFile);
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

        createUploadWindow();
    	
    });
    // /END ## upload_subscriptions #################################################
    
    // /Start ## Add Condition ##############################################
    $('div.modal-content').on( 'click', 'button.add_condition', function (event) {

    	event.stopPropagation();
        event.preventDefault();
    	            
        var condition = {
    				  "conditions" : [
    					  {
    						  "jmespath" : ko.observable("")
    					  }
    			      ]
    				}
                
        // Not sure if its correct to use index 0(zero) here,, is it correct??
        vm.subscription()[0].requirements.push(condition);
    });
    // /Stop ## Add Condition ################################################


    // /Start ## Delete Condition ##############################################
    $('div.modal-content').on( 'click', 'button.condition_delete', function (event) {

    	event.stopPropagation();
        event.preventDefault();

        var context = ko.contextFor(event.target);
        var indexToRemove = context.$index();
        
        // Removing Requirement(Condition), based on index position, from Requirement form in Add_Subscription window.
        vm.subscription()[0].requirements.splice(indexToRemove,1);

    });
    // /Stop ## Delete Condition ################################################

    
    // /Start ## Add Subscription ########################################
    $('.container').on( 'click', 'button.btn.btn-success.add_subscription', function (event) {

        event.stopPropagation();
        event.preventDefault();
        
        // Clear observable array
        vm.subscription([]);        

        // Map JSON to Model and observableArray
        var mappedPackageInfo = $.map(default_json_empty, function (item) {
        	
        	// Removing old Requirements and conditions from previous Add_subscription window.
        	item.requirements.splice(1,item.requirements.length - 1);
        	
        	// Defining Observable on all parameters in Requirements array(which is defined as ObservableArray)
        	item.requirements[0].conditions[0] = {"jmespath" : ko.observable("")};
        	item.requirements[0].type = ko.observable("");

            return new subscription_model(item);
        });
        
        // Load data into observable array
        vm.subscription(mappedPackageInfo);

        save_method = 'add';

        $('.form-group').removeClass('has-error'); // clear error class
        $('.help-block').empty(); // clear error string

        $('#modal_form').modal('show'); // show bootstrap modal
        $('.modal-title').text('Add Subscription'); // Set Title to Bootstrap modal title

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

                var returnData = [data];
                if (returnData.length > 0) {

                    // Map JSON to Model and observableArray
                    var mappedPackageInfo = $.map(returnData, function (item) {
                    	
                    	// Defining Observable on all parameters in Requirements array(which is defined as ObservableArray)
                        for (i=0; i < item[0].requirements.length; i++){
                            var jmespath_temp = item[0].requirements[i].conditions[0].jmespath;
                            item[0].requirements[i].conditions[0] = {"jmespath" : ko.observable(jmespath_temp)};
                            
                            var type_temp = item[0].requirements[i].type;
                            item[0].requirements[i].type = ko.observable(type_temp);
                        }
                    	
                        return new subscription_model(item[0]);
                    });

                    // Load data into observable array
                    vm.subscription(mappedPackageInfo);


                    $('#modal_form').modal('show');
                    $('.modal-title').text('Subscription: ' + data.subscriptionName);

                    save_method = 'edit';
                }

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
        ajaxHttpSender.sendAjax(backendServiceUrl + "/subscriptions/"+id, "GET", null, callback);

    });
    // /Stop ## Edit Subscription ###########################################


    // /Start ## Save Subscription ##########################################
    $('div.modal-content').on( 'click', 'button.save_record', function (event) {

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
        	if (requirementsArray[i].conditions[0].jmespath() == "") {
            	$.jGrowl("Error: jmepath field must have a value", {
                	sticky : true,
                	theme : 'Error'
            	});
            	return;
        	}
        }
        //END: Check of other subscription fields values

        var id = ko.toJSON(vm.subscription()[0].subscriptionName).trim();

        var url;
        var type;
        if(save_method === 'add') {  // Add new
            url = backendServiceUrl + "/subscriptions";
            type = "POST";

        } else {  // Update existing
            url = backendServiceUrl + "/subscriptions";
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

        // Fetch Date and format
        var now = new Date();
        var nowStr = now.format("isoDate") + ' ' + now.format("isoTime");

        // Update property created with datetime (formatted)
        vm.subscription()[0].created(String(nowStr));

        // Perform AJAX
        var ajaxHttpSender = new AjaxHttpSender();
        ajaxHttpSender.sendAjax(url, type, ko.toJSON(vm.subscription()[0]), callback);



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
                     ajaxHttpSender.sendAjax(backendServiceUrl + "/subscriptions/"+id, "DELETE", null, callback);
                 },
                 cancel: function () {
                 }
             }
         });

    });
    // /Stop ## Delete Subscription #########################################



});  // $(document).ready(function() {




