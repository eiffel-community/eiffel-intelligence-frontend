function messageModel (message) {
    this.message = ko.observable(message);
}
function viewModel (data) {
    var self = this;
    self.errorMessages = ko.observableArray([]);
    var storedOld = JSON.parse(sessionStorage.getItem('ei.errorMessages') || '[]');
    var storedNew = JSON.parse(sessionStorage.getItem('ei.errorMessagesNew') || '[]');

    self.init = function() {
        var json = storedOld.concat(storedNew);
        for(var i = 0; i < json.length; i++) {
            self.addErrorMessage(json[i].message);
        }
    }
    self.addErrorMessage = function (data) {
        var model = new messageModel(data);
        self.errorMessages.push(model);
    };
    self.storeErrorMessage = function (data) {
        storedNew.push({"message": data})
        sessionStorage.setItem('ei.errorMessagesNew', JSON.stringify(storedNew));
    }
    self.mergeErrorMessages = function () {
        var json = storedOld.concat(storedNew);
        sessionStorage.setItem('ei.errorMessages', JSON.stringify(json));
        sessionStorage.setItem('ei.errorMessagesNew', '[]');
    }
    self.expandMessage = function (data, event) {
        if(event.target.classList.contains("white-space-normal")) {
            vm.resetExpandMessage();
        } else {
            vm.resetExpandMessage();
            event.target.classList.add("white-space-normal");
        }
    }
    self.resetExpandMessage = function () {
        $("#alerts").children("a").removeClass("white-space-normal");
    }
    self.stopPropagation = function () {
        $('a.alert-message').on('click', function (event) {
            event.stopPropagation();
            event.preventDefault();
        });
    }
}
var vm = new viewModel();
vm.init();
ko.cleanNode($("#alerts")[0]);
ko.applyBindings(vm,$("#alerts")[0]);

function logMessages (message) {
    $.jGrowl(message, {sticky: false, theme: 'Error', position:'center'});
    vm.addErrorMessage(message);
    vm.storeErrorMessage(message);
    vm.stopPropagation();
}

vm.stopPropagation();

$('#alertsDropdown').on('click', function (event) {
    vm.resetExpandMessage();
    vm.mergeErrorMessages();
});