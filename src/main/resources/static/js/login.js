
jQuery(document).ready(function() {

    // /Start ## Global AJAX Sender function ##################################
    var AjaxHttpSender = function () {};

    AjaxHttpSender.prototype.sendAjax = function (url, type, data, callback) {
        $.ajax({
            url : url,
            type : type,
            data : data,
            contentType : 'application/json; charset=utf-8',
            dataType : "json",
            cache: false,
            beforeSend : function () {
                callback.beforeSend();
            },
            error : function (XMLHttpRequest, textStatus, errorThrown) {
                callback.error(XMLHttpRequest, errorThrown);
            },
            success : function (responseData, textStatus) {
                callback.success(data);
            },
            complete : function (XMLHttpRequest, textStatus) {
                callback.complete();
            }
        });
    }
    // /Stop ## Global AJAX Sender function ##################################

    // /Start ## Cookies functions ###########################################
    function setCookie(name, value) {
        var expiry = new Date(new Date().getTime() + 30 * 24 * 3600 * 1000); // plus 30 days
        if(window.location.protocol == "https:") {
            document.cookie = name + "=" + escape(value) + "; path=/; expires=" + expiry.toGMTString() + "; secure; HttpOnly";
        } else {
            document.cookie = name + "=" + escape(value) + "; path=/; expires=" + expiry.toGMTString();
        }
    }

    function saveCookies(data, remember) {
        if(remember) {
            setCookie("ei-username", JSON.parse(data).username);
            setCookie("ei-password", JSON.parse(data).password);
        }
    }

    function getCookie(name) {
        var re = new RegExp(name + "=([^;]+)");
        var value = re.exec(document.cookie);
        return (value != null) ? unescape(value[1]) : null;
    }

    function getValuesFromCookie(key) {
        var value = "";
        if(cookie = getCookie(key)) {
            value = cookie;
        }
        return value;
    }
    // /Stop ## Cookies functions ############################################

    // /Start ## Knockout ####################################################
    function loginModel() {
        this.userState = {
            username: ko.observable(getValuesFromCookie("ei-username")),
            password: ko.observable(getValuesFromCookie("ei-password"))
        };
        this.remember = ko.observable(false);

        this.login = function(userState, remember) {
            var callback = {
                beforeSend : function () {},
                success : function (data) {
                    $.jGrowl("Welcome", {
                        sticky : false,
                        theme : 'Notify'
                    });
                    saveCookies(data, remember);
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
                var ajaxHttpSender = new AjaxHttpSender();
                ajaxHttpSender.sendAjax("/auth/login", "POST", dataJSON, callback);
            }
        }
    }

    var observableObject = $('#viewModelDOMObject')[0];
    ko.cleanNode(observableObject);
    var model = new loginModel();
    ko.applyBindings(model, document.getElementById("viewModelDOMObject"));
    // /Stop ## Knockout #####################################################

});