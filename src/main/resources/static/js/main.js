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

    var eiffelDocumentationUrlLinks = $('#eiffelDocumentationUrlLinks').text();
    var frontendServiceUrl = $('#frontendServiceUrl').text();

    $("#logoutBtn").click(function () {
        $.ajax({
            url: frontendServiceUrl + "/auth/logout",
            type: "GET",
            contentType: 'application/json; charset=utf-8',
            cache: false,
            complete: function (XMLHttpRequest, textStatus) {
                doIfUserLoggedOut();
                router.navigate('*');
            }
        });
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
