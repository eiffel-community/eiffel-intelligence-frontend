/*
This js file intend to keep the status information about the current selected back-end
and depending weather the back-end status has changed and or is online or not will
enable or disable different kinds of status icons/fields.
It may also fore page refresh pages, remove subscriptions from subscription list
and reload subscriptions in subscription list depending on the back-end status.
*/

// Start ## Status Indicator ##

function checkBackendStatus() {
    var currentUrl = router._lastRouteResolved.url;
    currentUrl = currentUrl.replace(/^\//, '');
    var isWhitelistedPage = !stringContainsSubstring(getWhiteListedPages(), currentUrl);
    if (isWhitelistedPage) {
        return;
    }

    if (!isBackEndStatusUpdated()) {
        addStatusIndicator(statusType.WARNING, statusText.UNKNOWN_BACK_END_STATUS);
        return;
    }

    if (isBackEndStatusOk()) {
        removeStatusIndicator();
    } else {
        addStatusIndicator(statusType.DANGER, statusText.BACKEND_DOWN);
    }

    routeSpecificFunctions(currentUrl);
}

function routeSpecificFunctions(currentUrl) {
    var isStatusChanged = isBackEndStatusChanged();
    if (currentUrl === "subscriptions" || "") {
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
                functionsToExecuteIfUserIsLoggedOut();
                setBackEndStatusOk(true);
            } else {
                executeIfLdapIsDeactivated();
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
    ajaxHttpSender.sendAjax(backendEndpoints.CHECK_STATUS, "GET", null, callback, contentType, datatype);
}

function addStatusIndicator(statusType, statusText) {
    var contentElement = document.getElementsByClassName("content")[0];
    if (contentElement == undefined) {
        return;
    }

    var statusIndicator = contentElement.previousElementSibling;
    if (statusIndicator != null) {
        removeStatusIndicator();
    }
    $(".content").before("<div class=\"subscription-alert alert " + statusType + "\">" + statusText + "</div>");
}

function removeStatusIndicator() {
    var parentContent = $(".content")[0];
    if (parentContent != undefined) {
        $(parentContent.previousElementSibling).remove();
    }
}
// End ## Status Indicator ##

updateBackendStatus();
setBackEndStatusTimerInterval();