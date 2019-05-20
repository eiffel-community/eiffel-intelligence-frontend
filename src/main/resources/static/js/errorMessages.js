function messageModel(message) {
    this.message = ko.observable(message);
}
function viewModel(data) {
    var self = this;
    self.errorMessages = ko.observableArray([]);
    var storedOld = JSON.parse(sessionStorage.getItem('ei.errorMessages') || '[]');
    var storedNew = JSON.parse(sessionStorage.getItem('ei.errorMessagesNew') || '[]');
    self.newMessagesLength = ko.observable(storedNew.length);

    self.init = function () {
        var json = storedOld.concat(storedNew);
        for (var i = 0; i < json.length; i++) {
            self.addErrorMessage(json[i].message);
        }
    };
    self.addErrorMessage = function (data) {
        var model = new messageModel(data);
        self.errorMessages.push(model);
    };
    self.removeErrorMessage = function (index) {
        var length = self.errorMessages.length;
        var realIndex = length - 1 - index;
        self.errorMessages.splice(realIndex, 1);
        self.mergeErrorMessages();
    };
    self.removeAllErrorMessages = function () {
        self.errorMessages([]);
        sessionStorage.setItem('ei.errorMessages', '[]');
        sessionStorage.setItem('ei.errorMessagesNew', '[]');
    };
    self.storeErrorMessage = function (data) {
        storedNew.push({ "message": data });
        sessionStorage.setItem('ei.errorMessagesNew', JSON.stringify(storedNew));
        self.updateNewMessagesLength();
    };
    self.mergeErrorMessages = function () {
        storedOld = ko.toJS(self.errorMessages);
        storedNew = [];
        sessionStorage.setItem('ei.errorMessages', JSON.stringify(storedOld));
        sessionStorage.setItem('ei.errorMessagesNew', JSON.stringify(storedNew));
        self.updateNewMessagesLength();
    };
    self.updateNewMessagesLength = function () {
        self.newMessagesLength(storedNew.length);
    };
    self.expandMessage = function (event) {
        if (event.target.classList.contains("expand")) {
            event.target.classList.remove("expand");
        } else {
            self.resetExpandMessage();
            event.target.classList.add("expand");
        }
    };
    self.resetExpandMessage = function () {
        $(".alert-message").removeClass("expand");
    };
    self.stopPropagation = function () {
        $('.alert-menu').on('click', function (event) {
            event.stopPropagation();
            event.preventDefault();
        });
    };
}
var vm = new viewModel();
vm.init();
ko.cleanNode($("#alertsItem")[0]);
ko.applyBindings(vm, $("#alertsItem")[0]);
vm.stopPropagation();

function logMessages(message) {
    $.jGrowl(message, { sticky: false, theme: 'Error', position: 'center' });
    vm.addErrorMessage(message);
    vm.storeErrorMessage(message);
    vm.stopPropagation();
}

$('#alertsLink').on('click', function (event) {
    vm.resetExpandMessage();
    vm.mergeErrorMessages();
});