var errorsStore = new Array();
let messageErr = JSON.parse(sessionStorage.getItem('errorsStore'));
if(messageErr){
    for(var i=0; i<messageErr.length; i++){
        errorsStore.push(messageErr[i]);
    }
}
function viewModel(data){
    this.errorMessages = ko.observableArray(data);
}
function getErrors(){
    let observableObject = $("#alerts")[0];
    ko.cleanNode(observableObject);
    ko.applyBindings(new viewModel(errorsStore), observableObject);
}
function logMessages(messageErr){
    $.jGrowl(messageErr, {sticky: false, theme: 'Error'});
    errorsStore.push({message:messageErr});
    sessionStorage.setItem('errorsStore', JSON.stringify(errorsStore));
}