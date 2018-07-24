jQuery(document).ready(function() {
var frontendServiceUrl = $('#frontendServiceUrl').text();
var sendbtn = document.getElementById('switcher').disabled = true;
function singleInstanceModel(name, host, port, path, https, active) {
	this.name = ko.observable(name),
	this.host = ko.observable(host),
	this.port = ko.observable(port),
	this.path = ko.observable(path),
	this.https = ko.observable(https),
	this.active = ko.observable(active)
}
function multipleInstancesModel(data) {
	var self = this;
	var selected;
	self.instances = ko.observableArray();
	var json = JSON.parse(ko.toJSON(data));
	for(var i = 0; i < json.length; i++) {
		var obj = json[i];
		var instance = new singleInstanceModel(obj.name, obj.host, obj.port, obj.path, obj.https, obj.active);
		self.instances.push(instance);
	}
	self.checked = function(){
	    var sendbtn = document.getElementById('switcher').disabled = false;
        selected = JSON.parse(ko.toJSON(this));
	    return true;
	}
	self.removeInstance = function() {
		self.instances.remove(this);
    	$.ajax({
            url: frontendServiceUrl + "/switch-backend",
            type: "DELETE",
            data: ko.toJSON(self.instances),
            contentType: 'application/json; charset=utf-8',
            cache: false,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                errorsStore.add(XMLHttpRequest.responseText);
                $.jGrowl(XMLHttpRequest.responseText, {sticky: false, theme: 'Error'});
            },
            success: function (responseData, XMLHttpRequest, textStatus) {
                $.jGrowl("Backend instance was deleted", {sticky: false, theme: 'Notify'});
            }
        });
	}
	self.submit = function(instances) {
	    var json = JSON.parse(ko.toJSON(instances));
	    self.instances.removeAll();
	    for(var i = 0; i < json.length; i++){
	        var obj = json[i];
	        if(obj.active == true &&
	            !(obj.name == selected.name && obj.host == selected.host && obj.port == selected.port && obj.path == selected.path)){
	            obj.active = false;
	        }
	        var instance = new singleInstanceModel(obj.name, obj.host, obj.port, obj.path, obj.https, obj.active);
            self.instances.push(instance);
	    }
		$.ajax({
		    url: frontendServiceUrl + "/switch-backend",
			type: "POST",
			data: ko.toJSON(self.instances),
			contentType: 'application/json; charset=utf-8',
			cache: false,
			error: function (XMLHttpRequest, textStatus, errorThrown) {
			    errorsStore.add(XMLHttpRequest.responseText);
			    $.jGrowl(XMLHttpRequest.responseText, {sticky: false, theme: 'Error'});
			},
			success: function (responseData, XMLHttpRequest, textStatus) {
			    $(location).attr('href', frontendServiceUrl)
			}
	    });
	}
}
$.ajax({
	url: frontendServiceUrl + "/get-instances",
	type: "GET",
	contentType: 'application/json; charset=utf-8',
	cache: false,
	error: function (XMLHttpRequest, textStatus, errorThrown) {
	    errorsStore.add("Failure when trying to load backend instances");
		$.jGrowl("Failure when trying to load backend instances", {sticky: false, theme: 'Error'});
	},
	success: function (responseData, XMLHttpRequest, textStatus) {
		var observableObject = $("#instancesModel")[0];
        ko.cleanNode(observableObject);
        ko.applyBindings(new multipleInstancesModel(responseData), observableObject);
	}
})
});