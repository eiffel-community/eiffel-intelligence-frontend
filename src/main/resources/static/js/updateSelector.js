var frontendServiceUrl = $('#frontendServiceUrl').text();
var frontendServiceBackEndPath = "/backend";

function singleInstanceModel(name, host, port, path, https, active) {
    this.name = ko.observable(name),
    this.host = ko.observable(host),
    this.port = ko.observable(port),
    this.path = ko.observable(path),
    this.https = ko.observable(https),
    this.active = ko.observable(active),
    this.information = name.toUpperCase() + " - " + host + " " + port + "/" + path;
}

function viewModel(data) {
    var self = this;
    var currentName;
    self.instances = ko.observableArray();
    var json = JSON.parse(ko.toJSON(data));
    for(var i = 0; i < json.length; i++) {
        var obj = json[i];
        var instance = new singleInstanceModel(obj.name, obj.host, obj.port, obj.path, obj.https, obj.active);
        self.instances.push(instance);
        if(obj.active == true){
            currentName = obj.name;
        }
    }
    self.selectedActive = ko.observable(currentName);
    self.onChange = function(){
        if(typeof self.selectedActive() !== "undefined"){
            $.ajax({
                url: frontendServiceUrl + frontendServiceBackEndPath,
                type: "PUT",
                data: self.selectedActive(),
                contentType: 'application/json; charset=utf-8',
                cache: false,
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    window.logMessages(XMLHttpRequest.responseText);
                },
                success: function (responseData, XMLHttpRequest, textStatus) {
                    console.log("Response from IE front end back end: " + responseData.message);
                    $.jGrowl(responseData.message, {sticky: false, theme: 'Notify'});
                    $("#navbarResponsive").removeClass("show");
                    $("#selectInstances").visible();
                    $("#mainFrame").load("subscriptionpage.html");
                }
            });
        } else {
            $.jGrowl("Please chose backend instance", {sticky: false, theme: 'Error'});
        }
    }
}

function updateBackEndInstanceList() {
    $.ajax({
        url: frontendServiceUrl + frontendServiceBackEndPath,
        type: "GET",
        contentType: 'application/json; charset=utf-8',
        cache: false,
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            window.logMessages("Failure when trying to load backend instances");
        },
        success: function (responseData, XMLHttpRequest, textStatus) {
            var observableObject = $("#selectInstances")[0];
            ko.cleanNode(observableObject);
            ko.applyBindings(new viewModel(responseData),observableObject);
        }
    });
}
