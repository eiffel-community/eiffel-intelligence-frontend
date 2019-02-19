var frontendServiceUrl = $('#frontendServiceUrl').text();

function addBakcendParameter(url) {
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
    console.log("New url ==== " + url);
    return url;
}

// /Start ## Global AJAX Sender function ##################################
var AjaxHttpSender = function () { };

AjaxHttpSender.prototype.sendAjax = function (url, type, data, callback) {
    $.ajax({
        url: addBakcendParameter(url),
        type: type,
        data: data,
        contentType: 'application/json; charset=utf-8',
        dataType: "json",
        cache: false,
        beforeSend: function () {
            callback.beforeSend();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            callback.error(XMLHttpRequest, textStatus, errorThrown);
        },
        success: function (data, textStatus) {
            callback.success(data, textStatus);
        },
        complete: function (XMLHttpRequest, textStatus) {
            callback.complete();
        }
    });
}
// /Stop ## Global AJAX Sender function ##################################

function doIfUserLoggedIn(user) {
    localStorage.removeItem("currentUser");
    localStorage.setItem("currentUser", user);
    $("#userItem").show();
    $("#userItem").addClass("user-login");
    $("#ldapUserName").text(user);
    $("#loginBlock").hide();
    $("#logoutBlock").show();
    $(".show_if_authorized").show();
}

function doIfUserLoggedOut() {
    localStorage.removeItem("currentUser");
    $("#userItem").show();
    $("#userItem").removeClass("user-login");
    $("#ldapUserName").text("Guest");
    $("#loginBlock").show();
    $("#logoutBlock").hide();
    $(".show_if_authorized").hide();
    localStorage.setItem('errorsStore', []);
}

function doIfSecurityOff() {
    $("#userItem").hide();
    $("#ldapUserName").text("");
}

function checkBackendSecured() {
    $.ajax({
        url: addBakcendParameter(frontendServiceUrl + "/auth"),
        type: "GET",
        contentType: "application/string; charset=utf-8",
        error: function (data) {
            doIfSecurityOff();
        },
        success: function (data) {
            var isSecured = JSON.parse(ko.toJSON(data)).security;
            if (isSecured == true) {
                checkLoggedInUser();
            } else {
                doIfSecurityOff();
            }
        }
    });
}

function checkLoggedInUser() {
    $.ajax({
        url: addBakcendParameter(frontendServiceUrl + "/auth/login"),
        type: "GET",
        contentType: 'application/string; charset=utf-8',
        cache: false,
        error: function (request, textStatus, errorThrown) {
            doIfUserLoggedOut();
        },
        success: function (responseData, textStatus) {
            var user = JSON.parse(ko.toJSON(responseData)).user;
            doIfUserLoggedIn(user);
        }
    });
}