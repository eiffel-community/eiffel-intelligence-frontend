jQuery(document).ready(function () {
    var callback = {
        success: function (responseData, textStatus) {
            console.log(responseData);
            $("#rulesContent").text(JSON.stringify(responseData, null, 2));
        }
    };
    var ajaxHttpSender = new AjaxHttpSender();
    ajaxHttpSender.sendAjax(backendEndpoints.RULES, "GET", null, callback);
});