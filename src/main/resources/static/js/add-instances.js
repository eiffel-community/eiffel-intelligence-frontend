jQuery(document).ready(function() {

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
			if(dataJSON.host == "" || dataJSON.port == "") {
				$.jGrowl("Host and port fields cannot be empty", {sticky: false, theme: 'Error'});
			} else {
				$.ajax({
					url: "/add-instances",
					type: "POST",
					data: data,
					contentType: 'application/json; charset=utf-8',
					cache: false,
					error: function (XMLHttpRequest, textStatus, errorThrown) {
						$.jGrowl(XMLHttpRequest.responseText, {sticky: false, theme: 'Error'});
					},
					success: function (responseData, textStatus) {
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