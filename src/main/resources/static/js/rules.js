jQuery(document).ready(function () {
    var callback = {
        success: function (responseData, textStatus) {
            var path = responseData.path;
            var content = JSON.parse(responseData.content);
            $("#rulesPath").text("Rules Path: " + path);
            $("#rulesContent").text(JSON.stringify(content, null, 2));
        }
    };
    var ajaxHttpSender = new AjaxHttpSender();
    ajaxHttpSender.sendAjax(backendEndpoints.RULES, "GET", null, callback);
});