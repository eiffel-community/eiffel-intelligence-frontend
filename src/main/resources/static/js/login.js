
jQuery(document).ready(function() {

    // /Start ## Global AJAX Sender function ##################################
    var AjaxHttpSender = function () {};

    AjaxHttpSender.prototype.sendAjax = function (url, type, token, callback) {
        $.ajax({
            url : url,
            type : type,
            contentType : 'application/json; charset=utf-8',
            cache: false,
            beforeSend : function (request) {
                callback.beforeSend(request, token);
            },
            error : function (XMLHttpRequest, textStatus, errorThrown) {
                callback.error(XMLHttpRequest, errorThrown);
            },
            success : function (responseData, textStatus) {
                callback.success(responseData);
            },
            complete : function (XMLHttpRequest, textStatus) {
                callback.complete();
            }
        });
    }
    // /Stop ## Global AJAX Sender function ##################################

    // /Start ## Cookies functions ###########################################
    function setCookie(name, value) {
        var expiry = new Date(new Date().getTime() + 3 * 3600 * 1000); // plus 3 hours
        if(window.location.protocol == "https:") {
            document.cookie = name + "=" + escape(value) + "; path=/; expires=" + expiry.toGMTString() + "; secure; HttpOnly";
        } else {
            document.cookie = name + "=" + escape(value) + "; path=/; expires=" + expiry.toGMTString();
        }
    }

    function getCookie(name) {
        var re = new RegExp(name + "=([^;]+)");
        var value = re.exec(document.cookie);
        return (value != null) ? unescape(value[1]) : null;
    }
    // /Stop ## Cookies functions ############################################

    // /Start ## Knockout ####################################################
    function loginModel() {
        this.userState = {
            username: ko.observable(""),
            password: ko.observable("")
        };
        this.remember = ko.observable(false);

        this.login = function(userState, remember) {
            var callback = {
                beforeSend : function (xhr, data) {
                    xhr.setRequestHeader("Authorization", "Basic " + token);
                },
                success : function (data) {
                    $.jGrowl("Welcome", {
                        sticky : false,
                        theme : 'Notify'
                    });
                    if(remember) {
                        setCookie("sessionID", data);
                    }
                    $("#mainFrame").load("subscriptionpage.html");
                },
                error : function (XMLHttpRequest, errorThrown) {
                    $.jGrowl(JSON.parse(XMLHttpRequest.responseText).message, {
                        sticky : false,
                        theme : 'Error'
                     });
                },
                complete : function () {}
            };

            var dataJSON = ko.toJSON(userState);
            if(JSON.parse(dataJSON).username == "" || JSON.parse(dataJSON).password == "") {
                $.jGrowl("Username and password fields cannot be empty", {
                    sticky : false,
                    theme : 'Error'
                });
            } else {
                var token = window.btoa(JSON.parse(dataJSON).username + ":" + JSON.parse(dataJSON).password);
                var ajaxHttpSender = new AjaxHttpSender();
                ajaxHttpSender.sendAjax("/auth/login", "GET", token, callback);
            }
        }
    }

    var observableObject = $('#viewModelDOMObject')[0];
    ko.cleanNode(observableObject);
    var model = new loginModel();
    ko.applyBindings(model, document.getElementById("viewModelDOMObject"));
    // /Stop ## Knockout #####################################################

});