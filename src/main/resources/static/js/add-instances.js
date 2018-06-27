jQuery(document).ready(function() {
var frontendServiceUrl = $('#frontendServiceUrl').text();
function instanceModel() {
    var self = this;
	self.instance = {
	    name: ko.observable(""),
		host: ko.observable(""),
		port: ko.observable(""),
		path: ko.observable(""),
		https: ko.observable(false)
	};
	self.add = function(instance) {
		var data = ko.toJSON(instance);
		var dataJSON = JSON.parse(data);
		var port = parseInt(dataJSON.port);
		if(dataJSON.host == "" || dataJSON.name == "" || dataJSON.port == "") {
			$.jGrowl("Host, name and port fields cannot be empty", {sticky: false, theme: 'Error'});
		} else if (port < 1 || port > 65535){
		    $.jGrowl("Port value should be from 1 to 65535", {sticky: false, theme: 'Error'});
		} else {
			$.ajax({
				url: frontendServiceUrl + "/add-instances",
				type: "POST",
				data: data,
				contentType: 'application/json; charset=utf-8',
				cache: false,
				error: function (XMLHttpRequest, textStatus, errorThrown) {
					$.jGrowl(XMLHttpRequest.responseText, {sticky: false, theme: 'Error'});
				},
				success: function (responseData, XMLHttpRequest, textStatus) {
					$.jGrowl("Added new backend instance", {sticky: false, theme: 'Notify'});
					$("#mainFrame").load("switch-backend.html");
				}
			});
		}
	}
}
    var observableObject = $("#instanceModel")[0];
    ko.cleanNode(observableObject);
    ko.applyBindings(new instanceModel(), observableObject);
});