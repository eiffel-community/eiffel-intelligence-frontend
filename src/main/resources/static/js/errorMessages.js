var errorsStore = new Array();
if(sessionStorage.getItem('errorsStore')){
    let messageErr = JSON.parse(sessionStorage.getItem('errorsStore'));
    for(var i=0; i<messageErr.length; i++){
        errorsStore.push(messageErr[i]);
    }
}
function viewModel(data){
    this.errorMessages = ko.observableArray(data);
}
function getErrors(){
    let messageErr = JSON.parse(sessionStorage.getItem('errorsStore'));
    let observableObject = $("#qwerty")[0];
    ko.cleanNode(observableObject);
    ko.applyBindings(new viewModel(messageErr), observableObject);
}
function logMessages(messageErr){
    $.jGrowl(messageErr, {sticky: false, theme: 'Error'});
    errorsStore.push({message:messageErr});
    localStorage.setItem('errorsStore', JSON.stringify(errorsStore));
}