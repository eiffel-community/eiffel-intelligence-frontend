function messageModel (message) {
    this.message = ko.observable(message);
}
function viewModel (data) {
    var self = this;
    self.errorMessages = ko.observableArray([]);
    var errorsStore = new Array();

    self.init = function() {
        var storage = sessionStorage.getItem('errorsStore');
        if(storage != undefined && storage != "") {
            let msg = JSON.parse(storage);
            for(var i=0; i<msg.length; i++){
                errorsStore.push(msg[i]);
            }
        }
        var json = JSON.parse(ko.toJSON(errorsStore));
        for(var i = 0; i < json.length; i++) {
            var obj = json[i];
            self.addErrorMessage(obj.message);
        }
    }
    self.addErrorMessage = function (data) {
        let msgErr = new messageModel(data);
        self.errorMessages.push(msgErr);
    };
    self.storeErrorMessage = function (data) {
        errorsStore.push({message:data});
        sessionStorage.setItem('errorsStore', JSON.stringify(errorsStore));
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

function logMessages (messageErr) {
    $.jGrowl(messageErr, {sticky: false, theme: 'Error', position:'center'});
    vm.addErrorMessage(messageErr);
    vm.storeErrorMessage(messageErr);
    vm.stopPropagation();
}

vm.stopPropagation();

$('#alertsDropdown').on('click', function (event) {
    vm.resetExpandMessage();
});