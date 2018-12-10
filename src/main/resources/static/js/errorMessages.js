function messageModel (message) {
    this.message = ko.observable(message);
}
function viewModel (data) {
    var self = this;
    self.errorMessages = ko.observableArray([]);
    var storedOld = JSON.parse(sessionStorage.getItem('ei.errorMessages') || '[]');
    var storedNew = JSON.parse(sessionStorage.getItem('ei.errorMessagesNew') || '[]');
    self.newMessagesLength = ko.observable(storedNew.length);

    self.init = function() {
        var json = storedOld.concat(storedNew);
        for(var i = 0; i < json.length; i++) {
            self.addErrorMessage(json[i].message);
        }
    }
    self.addErrorMessage = function (data) {
        var model = new messageModel(data);
        self.errorMessages.push(model);
    }
    self.removeErrorMessage = function (index) {
        var length = self.errorMessages.length;
        var realIndex = length - 1 - index;
        self.errorMessages.splice(realIndex,1);
        self.mergeErrorMessages();
    }
    self.storeErrorMessage = function (data) {
        storedNew.push({"message": data})
        sessionStorage.setItem('ei.errorMessagesNew', JSON.stringify(storedNew));
        self.updateNewMessagesLength();
    }
    self.mergeErrorMessages = function () {
        storedOld = ko.toJS(self.errorMessages);
        storedNew = [];
        sessionStorage.setItem('ei.errorMessages', JSON.stringify(storedOld));
        sessionStorage.setItem('ei.errorMessagesNew', JSON.stringify(storedNew));
        self.updateNewMessagesLength();
    }
    self.updateNewMessagesLength = function () {
        self.newMessagesLength(storedNew.length);
    }
    self.expandMessage = function (event) {
        if(event.target.classList.contains("white-space-normal")) {
            event.target.classList.remove("white-space-normal");
        } else {
            self.resetExpandMessage();
            event.target.classList.add("white-space-normal");
        }
    }
    self.resetExpandMessage = function () {
        $("#alerts").children().children(".dropdown-item").removeClass("white-space-normal");
    }
    self.stopPropagation = function () {
        $('i.fa-minus-circle').on('click', function (event) {
            event.stopPropagation();
            event.preventDefault();
        });
        $('div.message-block').on('click', function (event) {
            event.stopPropagation();
            event.preventDefault();
        });
    }
}
var vm = new viewModel();
vm.init();
ko.cleanNode($("#alertsParent")[0]);
ko.applyBindings(vm,$("#alertsParent")[0]);
vm.stopPropagation();

function logMessages (message) {
    $.jGrowl(message, {sticky: false, theme: 'Error', position:'center'});
    vm.addErrorMessage(message);
    vm.storeErrorMessage(message);
    vm.stopPropagation();
}

$('#alertsDropdown').on('click', function (event) {
    vm.resetExpandMessage();
    vm.mergeErrorMessages();
});