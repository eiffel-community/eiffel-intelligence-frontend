var router = new Navigo(null, true, '#');
var frontendServiceUrl = $('#frontendServiceUrl').text();
var frontendServiceBackEndPath = "/backend";
var timerInterval;

function addBackendParameter(url) {
    if (!sessionStorage.selectedActive) {
        return url;
    }
    var delimiter = "";
    var parameterKey = "backendname";

    if (url.includes("?")) {
        delimiter = "&";
    } else {
        delimiter = "?";
    }
    url = url + delimiter + parameterKey + "=" + sessionStorage.selectedActive;
    return url;
}

// /Start ## Global AJAX Sender function ##################################
var AjaxHttpSender = function () { };

// This function is to be used for every rest call going through the bridge except drawTable
// in subscription.js.
// In callback beforeSend, complete, error and success is optional.
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
            if(typeof callback.beforeSend === 'function') {
                callback.beforeSend(XMLHttpRequest);
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            if(typeof callback.error === 'function') {
                callback.error(XMLHttpRequest, textStatus, errorThrown);
            }
        },
        success: function (responseData, textStatus) {
            if(typeof callback.success === 'function') {
                callback.success(responseData, textStatus);
            }
        },
        complete: function (XMLHttpRequest, textStatus) {
            if(typeof callback.complete === 'function') {
                callback.complete(XMLHttpRequest, textStatus);
            }
        }
    });
}
// /Stop ## Global AJAX Sender function ##################################

function formatUrl(host, port, useHttps, contextPath) {
    var protocol = "http";
    if (useHttps) {
        protocol = "https";
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

    return protocol + "://" + host + port + contextPath;
}

// Start ## Routing ##
var routes = {};
routes["subscriptions"] = function () {
    updateBackEndInstanceList();
    $(".app-header").removeClass("header-bar-hidden");
    $(".main").load("subscriptionpage.html");
};
routes["test-rules"] = function () {
    updateBackEndInstanceList();
    $(".app-header").removeClass("header-bar-hidden");
    $(".main").load("testRules.html");
};
routes["ei-info"] = function () {
    updateBackEndInstanceList();
    $(".app-header").removeClass("header-bar-hidden");
    $(".main").load("eiInfo.html");
};
routes["switch-backend"] = function () {
    $(".app-header").addClass("header-bar-hidden");
    $(".main").load("switch-backend.html");
};
routes["add-backend"] = function () {
    $(".app-header").addClass("header-bar-hidden");
    $(".main").load("add-instances.html");
};
routes["login"] = function () {
    updateBackEndInstanceList();
    $(".app-header").removeClass("header-bar-hidden");
    $(".main").load("login.html");
};

router.on({
    'subscriptions': routes["subscriptions"],
    'test-rules': routes["test-rules"],
    'ei-info': routes["ei-info"],
    'switch-backend': routes["switch-backend"],
    'add-backend': routes["add-backend"],
    'login': routes["login"],
    '*': function () {
        router.navigate('subscriptions');
    }
}).resolve();

function reloadRoute() {
    const currentUrl = router._lastRouteResolved.url;
    routes[currentUrl]();
}

function navigateRoute(route) {
    router.navigate(route);
}
// End ## Routing ##

// Start ## Load Back end list ##
function updateBackEndInstanceList() {
    $.ajax({
        url: frontendServiceUrl + frontendServiceBackEndPath,
        type: "GET",
        contentType: 'application/json; charset=utf-8',
        cache: false,
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            window.logMessages("Failure when trying to load backend instances");
        },
        success: function (responseData, XMLHttpRequest, textStatus) {
            var observableObject = $("#selectInstances")[0];
            ko.cleanNode(observableObject);
            ko.applyBindings(new viewModel(responseData), observableObject);
        }
    });
}

function singleInstanceModel(name, host, port, contextPath, https, active) {
    this.name = ko.observable(name),
        this.host = ko.observable(host),
        this.port = ko.observable(port),
        this.contextPath = ko.observable(contextPath),
        this.https = ko.observable(https),
        this.active = ko.observable(active),
        this.information = name.toUpperCase() + " - " + host + " " + port + "/" + contextPath;
}

function viewModel(backendInstanceData) {
    var self = this;
    self.instances = ko.observableArray();
    var jsonBackendInstanceData = JSON.parse(ko.toJSON(backendInstanceData));

    for (var i = 0; i < jsonBackendInstanceData.length; i++) {
        var instanceData = jsonBackendInstanceData[i];
        var isActive = false;
        var name = instanceData.name;
        var host = instanceData.host;
        var port = instanceData.port;
        var https = instanceData.https;
        var contextPath = instanceData.contextPath;

        var thisInstanceShouldBeSelectedAsActive =
            instanceData.defaultBackend == true && !sessionStorage.selectedActive ||
            sessionStorage.selectedActive && sessionStorage.selectedActive == name;

        if (thisInstanceShouldBeSelectedAsActive) {
            isActive = true;
            sessionStorage.selectedActive = name;
        }

        sessionStorage.setItem(name, formatUrl(host, port, https, contextPath));
        var singleInstance = new singleInstanceModel(name, host, port, contextPath, https, isActive);
        self.instances.push(singleInstance);
    }

    self.selectedActive = ko.observable(sessionStorage.selectedActive);

    self.onChange = function () {
        if (typeof self.selectedActive() !== "undefined") {
            sessionStorage.selectedActive = self.selectedActive();
            location.reload();
        } else {
            $.jGrowl("Please choose backend instance", { sticky: false, theme: 'Error' });
        }
    }
}
// End ## Load Back end list ##

// Start ## Login and Security ##
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
        }
    };
    var ajaxHttpSender = new AjaxHttpSender();
    var contextPath = "/auth";
    ajaxHttpSender.sendAjax(contextPath, "GET", null, callback);
}

function checkLoggedInUser() {
    var callback = {
        success: function (responseData, textStatus) {
            var userFromBackEnd = responseData.user;
            doIfUserLoggedIn(userFromBackEnd);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            doIfUserLoggedOut();
        }
    };
    var ajaxHttpSender = new AjaxHttpSender();
    var contextPath = "/auth/login";
    ajaxHttpSender.sendAjax(contextPath, "GET", null, callback);
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
    backend_down: "<strong>Back end is down!</strong> Wait for it go up or switch to another back end before continuing!",
    test_rules_disabled: "<strong>Test Rule service is disabled!</strong> To enable it set the backend property [testaggregated.enabled] as [true]"
}

function addStatusIndicator(statusType, statusText) {
    var statusIndicator = $(".content")[0].previousElementSibling;
    if (statusIndicator != null) {
        $($(".content")[0].previousElementSibling).remove();
    }
    $(".content").before("<div class=\"subscription-alert alert " + statusType + "\">" + statusText + "</div>");
}

function removeStatusIndicator() {
    $($(".content")[0].previousElementSibling).remove();
}
// End ## Status Indicator ##
