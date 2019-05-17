jQuery(document).ready(function () {
	var frontendServiceUrl = $('#frontendServiceUrl').text();
	var frontendServicePath = "/backend";
	function instanceModel() {

		var self = this;
		self.instance = {
			name: ko.observable(""),
			host: ko.observable(""),
			port: ko.observable(""),
			contextPath: ko.observable(""),
			https: ko.observable(false)
		};
		self.add = function (instance) {
			var data = ko.toJSON(instance);
			var dataJSON = JSON.parse(data);
			var port = parseInt(dataJSON.port);
			if (dataJSON.host == "" || dataJSON.name == "" || dataJSON.port == "") {
				$.jGrowl("Host, name and port fields cannot be empty", { sticky: false, theme: 'Error' });
			} else if (port < 1 || port > 65535) {
				$.jGrowl("Port value should be from 1 to 65535", { sticky: false, theme: 'Error' });
			} else {
				$.ajax({
					url: frontendServiceUrl + frontendServicePath,
					type: "POST",
					data: data,
					contentType: 'application/json; charset=utf-8',
					cache: false,
					error: function (XMLHttpRequest, textStatus, errorThrown) {
						window.logMessages(JSON.parse(XMLHttpRequest.responseText)["message"]);
					},
					success: function (responseData, XMLHttpRequest, textStatus) {
						$.jGrowl(responseData.message, { sticky: false, theme: 'Notify' });
						navigateToRoute('switch-backend');
					}
				});
			}
		}
	}
	var observableObject = $("#instanceModel")[0];
	ko.cleanNode(observableObject);
	ko.applyBindings(new instanceModel(), observableObject);
});
