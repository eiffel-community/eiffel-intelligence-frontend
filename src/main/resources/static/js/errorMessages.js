var frontendServiceUrl = $('#frontendServiceUrl').text();
var errorsStore = (function(){
    return {
        add : function(errorMessage){
            console.log(errorMessage);
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
});}