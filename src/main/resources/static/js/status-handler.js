/*
This js file intend to keep the status information about the current selected back-end
and depending weather the back end status has changed and or is online or not will
enable or disable different kinds of status icons/fields.
It may also fore page refresh pages, remove subscriptions from subscription list
and reload subscriptions in subscription list depending on the back-end status.
*/
var pagesForStatusIndication = "subscriptions test-rules ei-info login";

var statusType = {
    success: "alert-success",
    info: "alert-info",
    warning: "alert-warning",
    danger: "alert-danger"
};

var statusText = {
    backend_down: "<strong>Back end is down!</strong> Wait for it go up or switch to another back end before continuing!",
    test_rules_disabled: "<strong>Test Rule service is disabled!</strong> To enable it set the backend property [testaggregated.enabled] as [true]"
};

var backEndStatus = true;
var previousBackEndStatus;
var backEnsStatusTimerInterval;

// Start ## getters and setters

function isBackEndStatusOk() {
    return Boolean(backEndStatus);
}

function setBackEndStatusOk(value) {
    backEndStatus = Boolean(value);
}

function isBackEndStatusChanged() {
    if (previousBackEndStatus === undefined) {
        previousBackEndStatus = backEndStatus;
        return false;
    }
    var statusChanged = previousBackEndStatus !== backEndStatus;
    previousBackEndStatus = backEndStatus;
    return Boolean(statusChanged);
}

function setBackEndStatusTimerInterval() {
    if (backEnsStatusTimerInterval === undefined) {
        backEnsStatusTimerInterval = window.setInterval(function () { updateBackendStatus(); }, 15000);
    }
}

// End   ## getters and setters

// Start ## Status Indicator ##

function checkBackendStatus() {
    const currentUrl = router._lastRouteResolved.url;
    var isWhitelistedPage = !stringContainsSubstring(pagesForStatusIndication, currentUrl);
    if (isWhitelistedPage) {
        return;
    }

    if (isBackEndStatusOk()) {
        removeStatusIndicator();
    } else {
        addStatusIndicator(statusType.danger, statusText.backend_down);
    }

    routeSpecificFunctions(currentUrl);
}

function routeSpecificFunctions(currentUrl) {
    var isStatusChanged = isBackEndStatusChanged();
    if (currentUrl === "subscriptions") {
        reDrawTable(isStatusChanged);
        updateConnectionButtonsStatus();

    }

    if (currentUrl !== "subscriptions") {
        reloadIfStatusChanged(isStatusChanged);
    }

}

function reDrawTable(isStatusChanged) {
    if (table === undefined) {
        return;
    }

    if(isStatusChanged && !isBackEndStatusOk()){
        table.clear().draw();
    }

    if(isStatusChanged && isBackEndStatusOk()) {
        table.ajax.reload(null, false);
    }
}

function updateConnectionButtonsStatus() {
    var EIConnBtn = document.getElementById("btnEIConnection");
    if (EIConnBtn !== null) {
        var red = "#ff0000";
        var green = "#00ff00";
        if (isBackEndStatusOk()) {
            EIConnBtn.style.background = green;
        } else {
            EIConnBtn.style.background = red;
        }
    }
}

function reloadIfStatusChanged(isStatusChanged) {
    if (isStatusChanged) {
        reloadRoute();
    }
}

function updateBackendStatus() {
    var callback = {
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            if (XMLHttpRequest.status == 401) {
                doIfUserLoggedOut();
                setBackEndStatusOk(true);
            } else {
                doIfSecurityOff();
                setBackEndStatusOk(false);
            }
        },
        success: function (data, textStatus) {
            checkBackendSecured();
            setBackEndStatusOk(true);
        },
        complete: function () {
            checkBackendStatus();
         }
    };
    var ajaxHttpSender = new AjaxHttpSender();
    var contentType = "application/string; charset=utf-8";
    var datatype = "text";
    var contextPath = "/auth/checkStatus";
    ajaxHttpSender.sendAjax(contextPath, "GET", null, callback, contentType, datatype);
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

updateBackendStatus();
setBackEndStatusTimerInterval();