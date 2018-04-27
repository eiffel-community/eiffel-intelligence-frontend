jQuery(document).ready(function() {
function singleInstanceModel(name, host, port, path, https, checked) {
	this.name = ko.observable(name),
	this.host = ko.observable(host),
	this.port = ko.observable(port),
	this.path = ko.observable(path),
	this.https = ko.observable(https),
	this.checked = ko.observable(checked)
}
function multipleInstancesModel(data) {
	var self = this;
	self.instances = ko.observableArray();
	var json = JSON.parse(data);
	for(var i = 0; i < json.length; i++) {
		var obj = json[i];
		var instance = new singleInstanceModel(obj.name, obj.host, obj.port, obj.path, obj.https, obj.checked);
		self.instances.push(instance);
	}
	self.removeInstance = function() {
		self.instances.remove(this);
    	    $.ajax({
           	    url: "/switch-backend",
            		type: "DELETE",
            		data: ko.toJSON(self.instances),
            		contentType: 'application/json; charset=utf-8',
            		cache: false,
            		error: function (XMLHttpRequest, textStatus, errorThrown) {
            		    $.jGrowl(XMLHttpRequest.responseText, {sticky: false, theme: 'Error'});
            		},
            		success: function (responseData, textStatus) {
            		    $.jGrowl("Backend instance was deleted", {sticky: false, theme: 'Notify'});
            			$("#mainFrame").load("switch-backend.html");
            		}
           		});
	}
	self.submit = function(instances) {
		$.ajax({
			url: "/switch-backend",
			type: "POST",
			data: ko.toJSON(instances),
			contentType: 'application/json; charset=utf-8',
			cache: false,
			error: function (XMLHttpRequest, textStatus, errorThrown) {
				$.jGrowl(XMLHttpRequest.responseText, {sticky: false, theme: 'Error'});
			},
			success: function (responseData, textStatus) {
				$.jGrowl("Backend instance was switched", {sticky: false, theme: 'Notify'});
				$("#mainFrame").load("subscriptionpage.html");
			}
			});
		}
	}
	$.ajax({
		url: "/get-instances",
		type: "GET",
		contentType: 'application/json; charset=utf-8',
		cache: false,
		error: function (XMLHttpRequest, textStatus, errorThrown) {
			$.jGrowl(XMLHttpRequest.responseText, {sticky: false, theme: 'Error'});
		},
		success: function (responseData, textStatus) {
			var observableObject = $("#instancesModel")[0];
            ko.cleanNode(observableObject);
            ko.applyBindings(new multipleInstancesModel(responseData), observableObject);

		}
	})
});