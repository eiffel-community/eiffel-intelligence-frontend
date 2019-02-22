var router = new Navigo(null, true, '#');
var frontendServiceUrl = $('#frontendServiceUrl').text();
var frontendServiceBackEndPath = "/backend";
var timerInterval;

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

function viewModel(data) {
    var self = this;
    var currentName;
    self.instances = ko.observableArray();
    var json = JSON.parse(ko.toJSON(data));
    var oldSelectedActive = self.selectedActive;
    for (var i = 0; i < json.length; i++) {
        var obj = json[i];
        var instance = new singleInstanceModel(obj.name, obj.host, obj.port, obj.contextPath, obj.https, obj.active);
        self.instances.push(instance);
        if (obj.active == true) {
            currentName = obj.name;
        }
    }
    self.selectedActive = ko.observable(currentName);
    self.onChange = function () {
        if (typeof self.selectedActive() !== "undefined") {
            $.ajax({
                url: frontendServiceUrl + frontendServiceBackEndPath,
                type: "PUT",
                data: self.selectedActive(),
                contentType: 'application/json; charset=utf-8',
                cache: false,
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    self.selectedActive = oldSelectedActive;
                    updateBackEndInstanceList();
                    window.logMessages(XMLHttpRequest.responseText);
                },
                success: function (responseData, XMLHttpRequest, textStatus) {
                    reloadRoute();
                }
            });
        } else {
            $.jGrowl("Please choose backend instance", { sticky: false, theme: 'Error' });
        }
    }
}
// End ## Load Back end list ##

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