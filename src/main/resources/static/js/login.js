jQuery(document).ready(function () {
    function rerouteToSubscription() {
        checkBackendSecured();
        if ( !isLdapEnabled() || isStringDefined(getCurrentUser()) ) {
            console.log("Ldap not enabled or user already logged in, this page should not be accessable!");
            navigateToRoute('subscriptions');
        }

    }
    rerouteToSubscription();

    // /Start ## Knockout ####################################################
    function loginModel() {
        this.userState = {
            ldapUserName: ko.observable(""),
            password: ko.observable("")
        };
        this.remember = ko.observable(false);

        this.login = function (userState, remember) {
            var dataJSON = JSON.parse(ko.toJSON(userState));
            if (dataJSON.ldapUserName == "" || dataJSON.password == "") {
                logMessage("Username and password fields cannot be empty");
            } else {
                var token = window.btoa(dataJSON.ldapUserName + ":" + dataJSON.password);
                sendLoginRequest(token);
            }
        };
    }

    function sendLoginRequest(token) {
        $('#loginError').hide();
        $('#loginError').removeClass("is-invalid");
        var callback = {
            beforeSend: function (XMLHttpRequest) {
                XMLHttpRequest.setRequestHeader("Authorization", "Basic " + token);
            },
            success: function (responseData, textStatus) {
                var currentUser = JSON.parse(ko.toJSON(responseData)).user;
                $.jGrowl("Welcome " + currentUser, { sticky: false, theme: 'Notify' });
                functionsToExecuteIfUserIsLoggedIn(currentUser);
                navigateToRoute('subscriptions');
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                var statusCode = XMLHttpRequest.status;

                // These error messages are not standardized.
                if (statusCode == 401) {
                    logMessage("Status code: 401, 'Unauthorized or bad credentials'");
                    $('#loginError').text("Invalid username and/or password!");
                } else if (statusCode == 500 && !isBackEndStatusOk()) {
                    logMessage("Status code: 500, Could not reach back-end");
                    $('#loginError').text("Back-end might be unavailable!");
                } else {
                    logMessage("Status Code: " + statusCode + ", 'Unknown server error'");
                }

                $('#loginError').addClass("is-invalid");
                $('#loginError').show();
            },
            complete: function () {
            }
        };
        var ajaxHttpSender = new AjaxHttpSender();
        ajaxHttpSender.sendAjax(backendEndpoints.LOGIN, "GET", "", callback);
    }

    var observableObject = $("#viewModelDOMObject")[0];
    ko.cleanNode(observableObject);
    var model = new loginModel();
    ko.applyBindings(model, observableObject);

    // Check EI Backend Server Status ########################################

    checkBackendStatus();

    // END OF EI Backend Server check #########################################
});
