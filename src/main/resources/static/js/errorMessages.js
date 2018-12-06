function messageModel(message){
    this.message = ko.observable(message);
}
function viewModel(data){
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
        console.log(data);
        let msgErr = new messageModel(data);
        self.errorMessages.push(msgErr);
    };
    self.storeErrorMessage = function (data) {
        errorsStore.push({message:data});
        sessionStorage.setItem('errorsStore', JSON.stringify(errorsStore));
    }
}
var vm = new viewModel();
vm.init();
ko.cleanNode($("#alerts")[0]);
ko.applyBindings(vm,$("#alerts")[0]);

function logMessages(messageErr){
    $.jGrowl(messageErr, {sticky: false, theme: 'Error', position:'center'});
    vm.addErrorMessage(messageErr);
    vm.storeErrorMessage(messageErr);
}