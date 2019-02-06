//Hide or show the sidebar.
//Large views and Medium or lower views have a different class toggle
//and act indepently of eachother.
$(document).on('click', '.navbar-toggler', function () {
    var sidebar = document.getElementsByClassName("sidebar");
    var intViewportWidth = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;

    if (intViewportWidth >= 768) {
        classToggleAll(sidebar, "sidebar-hidden");
    } else if (intViewportWidth < 768) {
        classToggleAll(sidebar, "sidebar-show");
    }
});

//Minimize or expand the sidebar
//The minimizer toggle only appears in large views.
$(document).on('click', '.sidebar-minimizer', function () {
    var sidebar = document.getElementsByClassName("sidebar");

    if (sidebar[0].classList.contains("sidebar-minimized")) {
        classToggleAll(sidebar, "sidebar-minimized");

        var collapsable = document.querySelectorAll(".sidebar-nav .nav .nav-item.dropdown .collapse");
        sidebarCollapseAttribute(collapsable, "data-toggle", "collapse");
    } else {
        classToggleAll(sidebar, "sidebar-minimized");

        var collapsable = document.querySelectorAll(".sidebar-nav .nav .nav-item.dropdown .collapse");
        sidebarCollapseAttribute(collapsable, "data-toggle", "button");
        sidebarCollapseAttribute(collapsable, "aria-expanded", "false");

        var collapseShow = document.querySelectorAll(".sidebar-nav .nav .nav-item.dropdown .collapse.show");
        classRemoveAll(collapseShow, "show");
    }
});

//At a certain media breakpoint the sidebar will hide if the user presses outside of the sidebar.
$(document).click(function (event) {
    var sidebarShow = document.querySelectorAll(".sidebar.sidebar-show");
    var target = $(event.target);
    var intViewportWidth = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;

    if (intViewportWidth < 768) {
        if (target.closest('.navbar-toggler').length == 0 && target.closest('.sidebar').length == 0 && sidebarShow.length > 0) {
            classRemoveAll(sidebarShow, "sidebar-show");
        }
    }
});

//At a certain media breakpoint the sidebar will hide if the user presses a link inside of the sidebar
//Dropdown menus don't count as links.
$(document).on('click', '.sidebar-show .sidebar-nav .nav-item:not(.dropdown) > .nav-link', function (e) {
    var sidebar = document.getElementsByClassName("sidebar");
    var intViewportWidth = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;

    if (intViewportWidth < 768) {
        classToggleAll(sidebar, "sidebar-show");
    }
});

//At a certain media breakpoint the sidebar will hide if the user presses a header dropdown.
//An extra addEventListener is needed for these since bootstrap stops propagation otherwise.
var navbarItems = document.querySelectorAll(".app-header .navbar-nav .nav-item.dropdown");
for (var i = 0; i < navbarItems.length; i++) {
    navbarItems[i].addEventListener('click', function () {
        var intViewportWidth = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
        if (intViewportWidth < 768) {
            var element = document.getElementsByClassName("sidebar");
            classRemoveAll(element, "sidebar-show");
        }
    }, true);
}

//Whenever a menu item is clicked any collapsed sub menus will hide.
$(document).on('click', '.sidebar-nav > .nav > .nav-item > .nav-link', function (e) {
    var collapseShow = document.querySelectorAll(".sidebar-nav .nav .nav-item.dropdown .collapse.show");
    sidebarCollapseToggle(collapseShow);
});

//When a transition for medium to large or the reverse is made then collapse toggle should
//be either added or removed depending on the sidebar-minimized class toggle.
var widthTransition = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
window.addEventListener('resize', function () {
    var breakpoint = 767.98;
    var widthCurrent = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
    var sidebarMinimized = document.querySelectorAll(".sidebar.sidebar-minimized");
    if (sidebarMinimized.length > 0) {
        var collapsable = document.querySelectorAll(".sidebar-nav .nav .nav-item.dropdown .collapse");
        if (widthCurrent > breakpoint && widthTransition <= breakpoint) {
            widthTransition = widthCurrent;
            sidebarCollapseAttribute(collapsable, "data-toggle", "button");
            sidebarCollapseAttribute(collapsable, "aria-expanded", "false");

            var collapseShow = document.querySelectorAll(".sidebar-nav .nav .nav-item.dropdown .collapse.show");
            classRemoveAll(collapseShow, "show");
        } else if (widthCurrent < breakpoint && widthTransition > breakpoint) {
            widthTransition = widthCurrent;
            sidebarCollapseAttribute(collapsable, "data-toggle", "collapse");
        }
    }
}, true);

function classToggleAll(items, className) {
    for (var i = 0; i < items.length; i++) {
        items[i].classList.toggle(className);
    }
}

function classAddAll(items, className) {
    for (var i = 0; i < items.length; i++) {
        items[i].classList.add(className);
    }
}

function classRemoveAll(items, className) {
    for (var i = 0; i < items.length; i++) {
        items[i].classList.remove(className);
    }
}

function sidebarCollapseAttribute(items, attribute, value) {
    if (items.length > 0) {
        for (var i = 0; i < items.length; i++) {
            items[i].previousElementSibling.setAttribute(attribute, value);
        }
    }
}

function sidebarCollapseToggle(items) {
    if (items.length > 0) {
        for (var i = 0; i < items.length; i++) {
            $(items[i]).collapse("toggle");
        }
    }
}