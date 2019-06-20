function stringContainsSubstring(string, substring) {
    var isSubstring = string.indexOf(substring) !== -1;
    return isSubstring;
}

function addBackendParameter(url) {
    var parameterKey = "backendname";

    if (!sessionStorage.selectedActive) {
        return url;
    }

    var delimiter = "?";
    if (stringContainsSubstring(url, delimiter)) {
        // url has delimeter ?, then delimeter should be &
        delimiter = "&";
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
    var url = addBackendParameter(getFrontEndServiceUrl() + contextPath);
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
};
// /Stop ## Global AJAX Sender function ##################################

// Start ## Common functions ##
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

function isString(value) {
    var isString = typeof value === 'string' || value instanceof String;
    return isString;
}

function isStringDefined(value) {
    var isDefined = false;
    if (isString(value)) {
        isDefined = value.length != 0;
    }
    return isDefined;
}

// /Stop ## Common functions ##################################

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

function navigateToRoute(route) {
    router.navigate(route);
}
// End ## Routing ##

// Start ## Load Back end list ##
function updateBackEndInstanceList() {
    $.ajax({
        url: getFrontEndServiceUrl() + getFrontendServiceBackEndPath(),
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

function singleInstanceModel(name, host, port, contextPath, https, active, defaultBackend) {
    this.name = ko.observable(name);
    this.host = ko.observable(host);
    this.port = ko.observable(port);
    this.contextPath = ko.observable(contextPath);
    this.https = ko.observable(https);
    this.active = ko.observable(active);
    this.defaultBackend = ko.observable(defaultBackend);
    this.information = name.toUpperCase() + " - " + host + " " + port + "/" + contextPath;
}

function getInstanceModels(jsonBackendInstanceData) {
    instanceModels = [];

    for (var i = 0; i < jsonBackendInstanceData.length; i++) {
        var instanceData = jsonBackendInstanceData[i];
        var isActive = false;
        var name = instanceData.name;
        var host = instanceData.host;
        var port = instanceData.port;
        var https = instanceData.https;
        var contextPath = instanceData.contextPath;
        var defaultBackend = instanceData.defaultBackend;

        var thisInstanceShouldBeSelectedAsActive =
            defaultBackend == true && !sessionStorage.selectedActive ||
            sessionStorage.selectedActive && sessionStorage.selectedActive == name;

        if (thisInstanceShouldBeSelectedAsActive) {
            isActive = true;
            sessionStorage.selectedActive = name;
        }

        sessionStorage.setItem(name, formatUrl(host, port, https, contextPath));
        var singleInstance = new singleInstanceModel(name, host, port, contextPath, https, isActive, defaultBackend);
        instanceModels.push(singleInstance);
    }
    return instanceModels;
}

function viewModel(backendInstanceData) {
    var self = this;

    var jsonBackendInstanceData = JSON.parse(ko.toJSON(backendInstanceData));
    var instanceModels = getInstanceModels(jsonBackendInstanceData);
    self.selectedActive = ko.observable(sessionStorage.selectedActive);

    self.instances = ko.observableArray();
    instanceModels.forEach(function (instanceModel) {
        self.instances.push(instanceModel);
    });

    self.onChange = function () {
        if (typeof self.selectedActive() !== "undefined") {
            sessionStorage.selectedActive = self.selectedActive();
            location.reload();
        } else {
            $.jGrowl("Please choose backend instance", { sticky: false, theme: 'Error' });
        }
    };
}
// End ## Load Back end list ##

// Start ## Login and Security ##
function functionsToExecuteIfUserIsLoggedIn(username) {
    setCurrentUser(username);
    showLoggedInUserInformation(username);
    unlockSubscriptionButtons();
}

function showLoggedInUserInformation(username) {
    $("#userItem").show();
    $("#login-nav-bar-icon").addClass("user-login-icon");
    $("#login-nav-bar-text").text(cropUsername(username));
    $("#ldapUserName").text(username);
    $("#loginBlock").hide();
    $("#logoutBlock").show();
}

function unlockSubscriptionButtons() {
    $(".logged-out-buttons").addClass("hidden_by_default");
    $(".logged-in-buttons").removeClass("hidden_by_default");
    $(".show_if_authorized").prop('disabled', false);
    $(".show_if_authorized").prop('title', "");
}

function cropUsername(username) {
    var ellipsis = "...";

    if (username.length <= getUsernameMaxDisplayLength()) {
        return username;
    }

    var breakUsernameAtIndex = getUsernameMaxDisplayLength() - ellipsis.length;
    return username.substr(0, breakUsernameAtIndex) + ellipsis;
}

function functionsToExecuteIfUserIsLoggedOut() {
    setCurrentUser("");
    showUserLoggedOutInformation();
    lockSubscriptionButtons();
    sessionStorage.setItem('errorsStore', []);
}

function showUserLoggedOutInformation() {
    $("#userItem").show();
    $("#login-nav-bar-icon").removeClass("user-login-icon");
    $("#login-nav-bar-text").text("Login");
    $("#ldapUserName").text("Guest");
    $("#loginBlock").show();
    $("#logoutBlock").hide();
}

function lockSubscriptionButtons() {
    $(".logged-out-buttons").removeClass("hidden_by_default");
    $(".logged-in-buttons").addClass("hidden_by_default");
    $(".show_if_authorized").prop('disabled', true);
    $(".show_if_authorized").prop('title', 'Please login to enable!');
}

function executeIfLdapIsDeactivated() {
    $("#userItem").hide();
    $("#ldapUserName").text("");
}

function checkBackendSecured() {
    var callback = {
        success: function (responseData, textStatus) {
            var ldapStatus = responseData.security;
            setLdapEnabled(ldapStatus);
            if (isLdapEnabled()) {
                checkLoggedInUser();
            } else {
                executeIfLdapIsDeactivated();
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            executeIfLdapIsDeactivated();
        }
    };
    var ajaxHttpSender = new AjaxHttpSender();
    ajaxHttpSender.sendAjax(backendEndpoints.AUTH, "GET", null, callback);
}

function checkLoggedInUser() {
    var callback = {
        success: function (responseData, textStatus) {
            var userFromBackEnd = responseData.user;
            functionsToExecuteIfUserIsLoggedIn(userFromBackEnd);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            functionsToExecuteIfUserIsLoggedOut();
        }
    };
    var ajaxHttpSender = new AjaxHttpSender();
    ajaxHttpSender.sendAjax(backendEndpoints.LOGIN, "GET", null, callback);
}

// End ## Login and Security ##
