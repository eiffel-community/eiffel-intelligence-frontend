var frontendServiceUrl = $('#frontendServiceUrl').text();
var errorsStore = (function(){
    return {
        add : function(errorMessage){
            $.post(frontendServiceUrl + "/addErrors", { message : errorMessage });
        },
    }
}());
function viewModel(data){
    this.errorMessages = ko.observableArray(data);
}
function getErrors(){
    $.get(frontendServiceUrl + "/getErrors", function(responseData){
        var observableObject = $("#alerts")[0];
        ko.cleanNode(observableObject);
        ko.applyBindings(new viewModel(responseData), observableObject);
    });
}
function logMessages(message){
    $.jGrowl(message, {sticky: false, theme: 'Error'});
    errorsStore.add(message);
}