var errorsStore = new Array();
let msg = JSON.parse(sessionStorage.getItem('errorsStore'));
if(msg){
    for(var i=0; i<msg.length; i++){
        errorsStore.push(msg[i]);
    }
}
function viewModel(message){
    this.message = ko.observable(message);
}
function model(data){
    var self = this;
    self.errorMessages = ko.observableArray([]);
    var json = JSON.parse(ko.toJSON(data));
    json.reverse();
    for(var i = 0; i < json.length; i++) {
        var obj = json[i];
    	let msgErr = new viewModel(obj.message);
    	self.errorMessages.push(msgErr);
    }
}
function logMessages(messageErr){
    $.jGrowl(messageErr, {sticky: false, theme: 'Error', position:'center'});
    errorsStore.push({message:messageErr});
    sessionStorage.setItem('errorsStore', JSON.stringify(errorsStore));
    $('div.dropdown-menu').replaceWith("<div id=\"alerts\" style=\"display: none;\" class=\"dropdown-menu\" aria-labelledby=\"alertsDropdown\" data-bind=\"foreach: errorMessages\">" +
                                            "<div class=\"dropdown-divider\"> </div>" +
                                                "<a class=\"dropdown-item\">" +
                                                    "<div class=\"dropdown-message small\" data-bind=\"text: message, attr: {title: message}\"></div>" +
                                                "</a>" +
                                        "</div>");
    ko.cleanNode($("#alerts")[0]);
    ko.applyBindings(new model(errorsStore),$("#alerts")[0]);
}
$(document).mouseup(function(e){
    var container = $("#alerts");
    var bell = $("i.fa.fa-fw.fa-bell");
    var click = $("#alertsDropdown");
    var x = document.getElementById("alerts");
    let msg = JSON.parse(sessionStorage.getItem('errorsStore'));
    if (!container.is(e.target) && container.has(e.target).length === 0 && x.style.display === "block"){
        container.hide();
    } else if(msg && x.style.display === "none" && (click.is(e.target) || bell.is(e.target))){
        container.show();
    }
});
ko.cleanNode($("#alerts")[0]);
ko.applyBindings(new model(errorsStore),$("#alerts")[0]);