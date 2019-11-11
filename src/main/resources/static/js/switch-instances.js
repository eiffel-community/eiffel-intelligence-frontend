/* global getFrontEndServiceUrl getFrontendServiceBackEndPath */
jQuery(document).ready(function () {
    document.getElementById("switcher").disabled = true;

    function multipleInstancesModel(backendInstanceData) {
        var self = this;
        var selected;

        var jsonBackendInstanceData = JSON.parse(ko.toJSON(backendInstanceData));
        var instanceModels = getInstanceModels(jsonBackendInstanceData);

        self.instances = ko.observableArray();
        instanceModels.forEach(function (instanceModel) {
            self.instances.push(instanceModel);
        });

        self.checked = function () {
            document.getElementById("switcher").disabled = false;
            selected = JSON.parse(ko.toJSON(this));
            return true;
        };

        self.submit = function () {
            sessionStorage.selectedActive = selected.name;
            navigateToRoute("subscriptions");
        };
    }

    $.ajax({
        url: getFrontEndServiceUrl() + getFrontendServiceBackEndPath(),
        type: "GET",
        contentType: "application/json; charset=utf-8",
        cache: false,
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            parseAndLogMessage(XMLHttpRequest.responseText);
        },
        success: function (responseData, XMLHttpRequest, textStatus) {
            var observableObject = $("#instancesModel")[0];
            ko.cleanNode(observableObject);
            ko.applyBindings(new multipleInstancesModel(responseData), observableObject);
        }
    });
});
