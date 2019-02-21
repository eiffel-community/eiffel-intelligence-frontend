jQuery(document).ready(function () {
    (function ($) {
        $.fn.invisible = function () {
            return this.each(function () {
                $(this).css("visibility", "hidden");
            });
        };
        $.fn.visible = function () {
            return this.each(function () {
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
            $(".app-header").removeClass("header-bar-hidden");
            $(".main").load("subscriptionpage.html");
        },
        'test-rules': function () {
            updateBackEndInstanceList();
            $(".app-header").removeClass("header-bar-hidden");
            $(".main").load("testRules.html");
        },
        'ei-info': function () {
            updateBackEndInstanceList();
            $(".app-header").removeClass("header-bar-hidden");
            $(".main").load("eiInfo.html");
        },
        'switch-backend': function () {
            $(".app-header").addClass("header-bar-hidden");
            $(".main").load("switch-backend.html");
        },
        'add-backend': function () {
            $(".app-header").addClass("header-bar-hidden");
            $(".main").load("add-instances.html");
        },
        'login': function () {
            updateBackEndInstanceList();
            $(".app-header").removeClass("header-bar-hidden");
            $(".main").load("login.html");
        },
        '*': function () {
            router.navigate('subscriptions');
        }
    }).resolve();

    $("#logoutBtn").click(function () {
        var callback = {
            beforeSend: function () {
            },
            success: function (responseData, textStatus) {
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function () {
                doIfUserLoggedOut();
                router.navigate('*');
            }
        };
        var ajaxHttpSender = new AjaxHttpSender();
        var contextPath = "/auth/logout";
        ajaxHttpSender.sendAjax(contextPath, "GET", null, callback);
    });

    function loadDocumentLinks() {
        // eiffelDocumentationUrlLinks variable is configure in application.properties
        var linksList = JSON.parse(eiffelDocumentationUrlLinks);
        var docLinksDoc = document.getElementById('docLinks');
        var liTag = null;
        var aTag = null;

        Object.keys(linksList).forEach(function (linkKey) {
            liTag = document.createElement('li');
            liTag.classList.add('nav-item');
            aTag = document.createElement('a');
            aTag.classList.add('nav-link');
            aTag.innerHTML = linkKey;
            aTag.setAttribute('href', linksList[linkKey]);
            aTag.setAttribute('target', '_blanc');
            aTag.setAttribute('rel', 'noopener noreferrer');
            liTag.appendChild(aTag);
            docLinksDoc.appendChild(liTag);
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

    function viewModel(backendInstanceData) {
        var self = this;
        var currentName;
        self.instances = ko.observableArray();
        var jsonBackendInstanceData = JSON.parse(ko.toJSON(backendInstanceData));

        for (var i = 0; i < jsonBackendInstanceData.length; i++) {
            var instanceData = jsonBackendInstanceData[i];
            var isActive = false;
            var name = instanceData.name;
            var host = instanceData.host;
            var port = instanceData.port;
            var https = instanceData.https;
            var contextPath = instanceData.contextPath;

            if ((instanceData.defaultBackend == true && !sessionStorage.selectedActive) ||
                (sessionStorage.selectedActive && sessionStorage.selectedActive == name)) {
                isActive = true;
                currentName = instanceData.name;
                sessionStorage.selectedActive = instanceData.name;
            }

            sessionStorage.setItem(name, formatUrl(host, port, https, contextPath));
            var singleInstance = new singleInstanceModel(name, host, port, contextPath, https, isActive);
            self.instances.push(singleInstance);
        }

        self.selectedActive = ko.observable(currentName);

        self.onChange = function () {
            if (typeof self.selectedActive() !== "undefined") {
                sessionStorage.selectedActive = self.selectedActive();
                location.reload();
            } else {
                $.jGrowl("Please choose backend instance", { sticky: false, theme: 'Error' });
            }
        }
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
                ko.applyBindings(new viewModel(responseData), observableObject);
            }
        });
    }

    function init() {
        checkBackendSecured();
        loadDocumentLinks();
    }

    init();

    $('body').on('click', function (e) {
        if ($(e.target).data('toggle') !== 'tooltip' && $(e.target)[0].className !== 'tooltip-inner') {
            $('.tooltip').tooltip('hide');
        }
    });
});
