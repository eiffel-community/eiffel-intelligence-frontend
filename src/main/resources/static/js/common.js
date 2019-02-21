var frontendServiceUrl = $('#frontendServiceUrl').text();

function addBackendParameter(url) {
    if (!sessionStorage.selectedActive) {
        return url;
    }
    var delimeter = "";
    var parameterKey = "backendname";

    if (url.includes("?")) {
        delimeter = "&";
    } else {
        delimeter = "?";
    }
    url = url + delimeter + parameterKey + "=" + sessionStorage.selectedActive;
    return url;
}

// /Start ## Global AJAX Sender function ##################################
var AjaxHttpSender = function () { };

// This function is to be used for every rest call going through the bridge except drawTable in subscription.js
AjaxHttpSender.prototype.sendAjax = function (contextPath, type, data, callback, contentType, dataType) {
    if (!contentType) {
        contentType = "application/json; charset=utf-8";
    }
    if (!dataType) {
        dataType = "json";
    }
    url = addBackendParameter(frontendServiceUrl+contextPath)
    $.ajax({
        url: url,
        type: type,
        data: data,
        contentType: contentType,
        dataType: dataType,
        cache: false,
        beforeSend: function (XMLHttpRequest) {
            callback.beforeSend(XMLHttpRequest);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            if (contextPath == "/auth") {
                console.log("Data Error /auth == " + XMLHttpRequest.message);
            }
            if (contextPath == "/auth/login") {
                console.log("Data Error /auth/login == " + XMLHttpRequest.message);
            }
            callback.error(XMLHttpRequest, textStatus, errorThrown);
        },
        success: function (responseData, textStatus) {
            if (contextPath == "/auth") {
                console.log("Data /auth == " + ko.toJSON(responseData));
            }
            if (contextPath == "/auth/login") {
                console.log("Data /auth/login == " + ko.toJSON(responseData));
            }
            callback.success(responseData, textStatus);
        },
        complete: function (XMLHttpRequest, textStatus) {
            callback.complete(XMLHttpRequest, textStatus);
        }
    });
}
// /Stop ## Global AJAX Sender function ##################################

function formatUrl(host, port, https, contextPath) {
    var http = "http";
    if (https == true || https == "https") {
        http  = http + "s";
    }
    if (contextPath) {
        if (contextPath.charAt(0) != "/"){
            contextPath = "/" + contextPath;
        }
    } else {
        contextPath = "";
    }

    if (port) {
        port = ":" + port;
    } else {
        port = "";
    }

    return http + "://" + host + port + contextPath;
}

function doIfUserLoggedIn(user) {
    sessionStorage.removeItem("currentUser");
    sessionStorage.setItem("currentUser", user);
    $("#userItem").show();
    $("#userItem").addClass("user-login");
    $("#ldapUserName").text(user);
    $("#loginBlock").hide();
    $("#logoutBlock").show();
    $(".show_if_authorized").show();
}

function doIfUserLoggedOut() {
    sessionStorage.removeItem("currentUser");
    $("#userItem").show();
    $("#userItem").removeClass("user-login");
    $("#ldapUserName").text("Guest");
    $("#loginBlock").show();
    $("#logoutBlock").hide();
    $(".show_if_authorized").hide();
    sessionStorage.setItem('errorsStore', []);
}

function doIfSecurityOff() {
    $("#userItem").hide();
    $("#ldapUserName").text("");
}

function checkBackendSecured() {
    var callback = {
        beforeSend: function () {
        },
        success: function (responseData, textStatus) {
            var isSecured = JSON.parse(ko.toJSON(responseData)).security;
            if (isSecured == true) {
                checkLoggedInUser();
            } else {
                doIfSecurityOff();
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            doIfSecurityOff();
        },
        complete: function () {
            console.log("Complete security check!");
        }
    };
    var ajaxHttpSender = new AjaxHttpSender();
    var contextPath = "/auth";
    ajaxHttpSender.sendAjax(contextPath, "GET", null, callback);
}

function checkLoggedInUser() {
    var callback = {
        beforeSend: function () {
        },
        success: function (responseData, textStatus) {
            var userFromBackEnd = responseData.user;
            doIfUserLoggedIn(userFromBackEnd);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            doIfUserLoggedOut();
        },
        complete: function () {
        }
    };
    var ajaxHttpSender = new AjaxHttpSender();
    var contextPath = "/auth/login";
    ajaxHttpSender.sendAjax(contextPath, "GET", null, callback);
}