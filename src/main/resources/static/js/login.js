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
                window.logMessages("Username and password fields cannot be empty");
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

                if (statusCode == 401) {
                    window.logMessages("Error Status code: 401 'Unauthorized or bad credentials'");
                    $('#loginError').text("Invalid username and/or password!");
                } else if (statusCode == 500 && !isBackEndStatusOk()) {
                    window.logMessages("Error back-end is not reachable!");
                    $('#loginError').text("Back-end might be unavailable!");
                } else {
                    window.logMessages("Error Status Code: " + statusCode + " 'Unknown server error'");
                }

                $('#loginError').addClass("is-invalid");
                $('#loginError').show();
            },
            complete: function () {
            }
        };
        var ajaxHttpSender = new AjaxHttpSender();
        var contextPath = "/auth/login";
        ajaxHttpSender.sendAjax(contextPath, "GET", "", callback);
    }

    var observableObject = $("#viewModelDOMObject")[0];
    ko.cleanNode(observableObject);
    var model = new loginModel();
    ko.applyBindings(model, observableObject);

    // Check EI Backend Server Status ########################################

    checkBackendStatus();

    // END OF EI Backend Server check #########################################
});
