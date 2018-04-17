
jQuery(document).ready(function() {

    // Fetch injected URL from DOM
    frontendServiceUrl = $('#frontendServiceUrl').text();

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
            success : function (data, textStatus) {

                callback.success();
            },
            complete : function (XMLHttpRequest, textStatus) {
                callback.complete();
            }
        });
    }
    // /Stop ## Global AJAX Sender function ##################################

    // /Start ## Knockout ####################################################
    function loginModel() {
        this.userState = {
            username: ko.observable(""),
            password: ko.observable(""),
            remember: ko.observable(false)
        };

        this.login = function(userState) {
            var callback = {
                beforeSend : function () {},
                success : function () {
                    $.jGrowl("Welcome", {
                        sticky : false,
                        theme : 'Notify'
                    });
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
                ajaxHttpSender.sendAjax(frontendServiceUrl + "/auth/login", "POST", dataJSON, callback);
            }
        }
    }

    var observableObject = $('#viewModelDOMObject')[0];
    ko.cleanNode(observableObject);
    var model = new loginModel();
    ko.applyBindings(model, document.getElementById("viewModelDOMObject"));
    // /Stop ## Knockout ####################################################

});