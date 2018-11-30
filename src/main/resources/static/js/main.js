jQuery(document).ready(function() {
    (function($) {
        $.fn.invisible = function() {
            return this.each(function() {
                $(this).css("visibility", "hidden");
            });
        };
        $.fn.visible = function() {
            return this.each(function() {
                $(this).css("visibility", "visible");
            });
        };
    }(jQuery));
    // Used for navigation/routing. First parameter is used to set the main URL but can be set to null.
    // Second is to set the use of hash to true or false. This uses and old routing approach with hash in the URL.
    // Third specifies the hash character you want to use.
    var router = new Navigo(null, true, '#');
    var eiffelDocumentationUrlLinks = $('#eiffelDocumentationUrlLinks').text();
    var frontendServiceUrl = $('#frontendServiceUrl').text();
    var frontendServiceBackEndPath = "/backend";

    router.on({
        'subscriptions': function () {
            updateBackEndInstanceList();
            $("#navbarResponsive").removeClass("show");
            $("#selectInstances").visible();
            $("#mainFrame").load("subscriptionpage.html");
        },
        'test-rules': function () {
            updateBackEndInstanceList();
            $("#navbarResponsive").removeClass("show");
            $("#selectInstances").visible();
            $("#mainFrame").load("testRules.html");
        },
        'ei-info': function () {
            updateBackEndInstanceList();
            $("#navbarResponsive").removeClass("show");
            $("#selectInstances").visible();
            $("#mainFrame").load("eiInfo.html");
        },
        'switch-backend': function () {
            $("#navbarResponsive").removeClass("show");
            $("#selectInstances").invisible();
            $("#mainFrame").load("switch-backend.html");
        },
        'add-backend': function () {
            $("#navbarResponsive").removeClass("show");
            $("#selectInstances").invisible();
            $("#mainFrame").load("add-instances.html");
        },
        'login': function () {
            updateBackEndInstanceList();
            $("#navbarResponsive").removeClass("show");
            $("#selectInstances").visible();
            $("#mainFrame").load("login.html");
        },
        '*': function () {
            router.navigate('subscriptions');
        }
    }).resolve();

    $("#logoutBtn").click(function() {
        $.ajax({
            url : frontendServiceUrl + "/auth/logout",
            type : "GET",
            contentType : 'application/json; charset=utf-8',
            cache: false,
            complete : function (XMLHttpRequest, textStatus) {
                doIfUserLoggedOut();
                router.navigate('*');
            }
        });
    });

    function doIfUserLoggedIn(currentUser) {
        localStorage.removeItem("currentUser");
        localStorage.setItem("currentUser", currentUser);
        $("#ldapUserName").text(currentUser);
        $("#logoutBlock").show();
        $(".show_if_authorized").show();
    }

    function doIfUserLoggedOut() {
        localStorage.removeItem("currentUser");
        $("#ldapUserName").text("Guest");
        $("#loginBlock").show();
        $("#logoutBlock").hide();
        $(".show_if_authorized").hide();
        localStorage.setItem('errorsStore', []);
    }

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
                ko.applyBindings(new viewModel(responseData),observableObject);
            }
        });
    }

    function checkBackendSecured() {
        $.ajax({
            url: frontendServiceUrl + "/auth",
            type: "GET",
            contentType: "application/string; charset=utf-8",
            error: function (data) {},
            success: function (data) {
                var isSecured = JSON.parse(ko.toJSON(data)).security;
                if (isSecured == true) {
                    checkLoggedInUser();
                }
            }
        });
    }

    function checkLoggedInUser() {
        $.ajax({
            url : frontendServiceUrl + "/auth/login",
            type : "GET",
            contentType : 'application/string; charset=utf-8',
            cache: false,
            error : function (request, textStatus, errorThrown) {
                doIfUserLoggedOut();
            },
            success : function (responseData, textStatus) {
                var user = JSON.parse(ko.toJSON(responseData)).user;
                doIfUserLoggedIn(user);
            }
        });
    }

    function loadDocumentLinks(){
        // eiffelDocumentationUrlLinks variable is configure in application.properties
        var linksList = JSON.parse(eiffelDocumentationUrlLinks);
        var docLinksDoc = document.getElementById('collapseDocPages');
        var liTag = null;
        var aTag = null;

        Object.keys(linksList).forEach(function(linkKey) {
            liTag = document.createElement('li');
            aTag = document.createElement('a');
            aTag.innerHTML = linkKey;
            aTag.setAttribute('href', linksList[linkKey]);
            aTag.setAttribute('target', '_blanc');
            liTag.appendChild(aTag);
            docLinksDoc.appendChild(liTag);
        });
    }

    function init() {
        checkBackendSecured();
        loadDocumentLinks();
    }

    init();

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
        for(var i = 0; i < json.length; i++) {
            var obj = json[i];
            var instance = new singleInstanceModel(obj.name, obj.host, obj.port, obj.contextPath, obj.https, obj.active);
            self.instances.push(instance);
            if(obj.active == true){
                currentName = obj.name;
            }
        }
        self.selectedActive = ko.observable(currentName);
        self.onChange = function(){
            if(typeof self.selectedActive() !== "undefined"){
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
                        console.log("Response from IE front end back end: " + responseData.message);
                        location.reload();
                    }
                });
            } else {
                $.jGrowl("Please chose backend instance", {sticky: false, theme: 'Error'});
              }
        }
    }

    $('body').on('click', function (e) {
        if ($(e.target).data('toggle') !== 'tooltip' && $(e.target)[0].className !== 'tooltip-inner') {
            $('.tooltip').tooltip('hide');
        }
    });
});
