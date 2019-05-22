jQuery(document).ready(function () {
    function reroutToSubscritpion() {
        checkBackendSecured();
        if (!isLdapEnabled() || currentUser != null && currentUser != undefined) {
            navigateToRoute('subscriptions');
        }

    }
    reroutToSubscritpion();

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
        var callback = {
            beforeSend: function (XMLHttpRequest) {
                XMLHttpRequest.setRequestHeader("Authorization", "Basic " + token);
            },
            success: function (responseData, textStatus) {
                var currentUser = JSON.parse(ko.toJSON(responseData)).user;
                $.jGrowl("Welcome " + currentUser, { sticky: false, theme: 'Notify' });
                doIfUserLoggedIn(currentUser);
                navigateToRoute('subscriptions');
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                window.logMessages("Bad credentials");
            },
            complete: function () {
            }
        };
        var ajaxHttpSender = new AjaxHttpSender();
        var contextPath = "/auth/login";
        ajaxHttpSender.sendAjax(contextPath, "GET", token, callback);
    }

    var observableObject = $("#viewModelDOMObject")[0];
    ko.cleanNode(observableObject);
    var model = new loginModel();
    ko.applyBindings(model, observableObject);
});
