var frontendServiceUrl = $('#frontendServiceUrl').text();
const errorsStore = [];
function viewModel(data){
    this.errorMessages = ko.observableArray(data);
}
function getErrors(){
    const messageErr = JSON.parse(localStorage.getItem('errorsStore'));
    var observableObject = $("#alerts")[0];
    ko.cleanNode(observableObject);
    ko.applyBindings(new viewModel(messageErr), observableObject);
}
function logMessages(messageErr){
    $.jGrowl(messageErr, {sticky: false, theme: 'Error'});
    errorsStore.push({message:messageErr});
    localStorage.setItem('errorsStore', JSON.stringify(errorsStore));
}