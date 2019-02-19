jQuery(document).ready(function () {
    var frontendServiceUrl = $('#frontendServiceUrl').text();
    var frontendServiceBackEndPath = "/backend";
    var sendbtn = document.getElementById('switcher').disabled = true;

    function singleInstanceModel(name, host, port, contextPath, https, active, defaultBackend) {
        this.name = ko.observable(name),
            this.host = ko.observable(host),
            this.port = ko.observable(port),
            this.contextPath = ko.observable(contextPath),
            this.https = ko.observable(https),
            this.active = ko.observable(active)
        this.defaultBackend = ko.observable(defaultBackend)
    }
    function multipleInstancesModel(data) {
        var router = new Navigo(null, true, '#');
        var self = this;
        var selected;
        self.instances = ko.observableArray();
        var json = JSON.parse(ko.toJSON(data));
        for (var i = 0; i < json.length; i++) {
            var obj = json[i];
            var instance = new singleInstanceModel(obj.name, obj.host, obj.port, obj.contextPath, obj.https, obj.active, obj.defaultBackend);
            self.instances.push(instance);
        }
        self.checked = function () {
            var sendbtn = document.getElementById('switcher').disabled = false;
            selected = JSON.parse(ko.toJSON(this));
            return true;
        }
        self.removeInstance = function () {
            self.instances.remove(this);
            $.ajax({
                url: frontendServiceUrl + frontendServiceBackEndPath,
                type: "DELETE",
                data: ko.toJSON(this),
                contentType: 'application/json; charset=utf-8',
                cache: false,
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    window.logMessages(XMLHttpRequest.responseText);
                },
                success: function (responseData, XMLHttpRequest, textStatus) {
                    $.jGrowl(responseData.message, { sticky: false, theme: 'Notify' });
                }
            });
        }
        self.submit = function () {
            sessionStorage.selectedActive = selected.name;
            router.navigate('subscriptions');
        }
    }
    $.ajax({
        url: frontendServiceUrl + frontendServiceBackEndPath,
        type: "GET",
        contentType: 'application/json; charset=utf-8',
        cache: false,
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            window.logMessages("Failure when trying to load backend instances");
        },
        success: function (responseData, XMLHttpRequest, textStatus) {
            var observableObject = $("#instancesModel")[0];
            ko.cleanNode(observableObject);
            ko.applyBindings(new multipleInstancesModel(responseData), observableObject);
        }
    })
});
