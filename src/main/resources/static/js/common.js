var frontendServiceUrl = $('#frontendServiceUrl').text();

// Start ## Login and Security ##
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
        url: frontendServiceUrl + "/auth",
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
        url: frontendServiceUrl + "/auth/login",
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
// End ## Login and Security ##

// Start ## Status Indicator ##
var statusType = {
    success: "alert-success",
    info: "alert-info",
    warning: "alert-warning",
    danger: "alert-danger"
};

var statusText = {
    backend_down: "<strong>Back end is down</strong>, wait for it go up or switch to another back end before continuing!"
}

function addStatusIndicator(statusType, statusText) {
    var statusIndicator = $(".content")[0].previousElementSibling;
    if(statusIndicator != null) {
        $($(".content")[0].previousElementSibling).remove();
    }
    $(".content").before("<div class=\"subscription-alert alert "+statusType+"\">"+statusText+"</div>");
}

function removeStatusIndicator() {
    $($(".content")[0].previousElementSibling).remove();
}
// End ## Status Indicator ##