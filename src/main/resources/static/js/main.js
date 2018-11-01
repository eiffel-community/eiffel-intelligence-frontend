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
    $("#selectInstances").visible();
	// Fetch injected URL from DOM
	var eiffelDocumentationUrlLinks = $('#eiffelDocumentationUrlLinks').text();
	var frontendServiceUrl = $('#frontendServiceUrl').text();
	var frontendServiceBackEndPath = "/backend";

	function updateSelector() {
		// Moved this to its own file so that we may use it in multiple .js files.
		$.getScript( "js/updateSelector.js" )
			.done(function( script, textStatus ) {
			updateBackEndInstanceList();
		});
	}

	function loadMainPage() {
		updateSelector();
		$("#navbarResponsive").removeClass("show");
	    $("#selectInstances").visible();
		$("#mainFrame").load("subscriptionpage.html");
	}

	$("#testRulesBtn").click(function() {
		updateSelector();
		$("#navbarResponsive").removeClass("show");
	    $("#selectInstances").visible();
		$("#mainFrame").load("testRules.html");
	});

	$("#eiInfoBtn").click(function() {
		updateSelector();
		$("#navbarResponsive").removeClass("show");
	    $("#selectInstances").visible();
		$("#mainFrame").load("eiInfo.html");
	});

	$("#loginBtn").click(function() {
		$("#navbarResponsive").removeClass("show");
	    $("#selectInstances").visible();
		$("#mainFrame").load("login.html");
	});

	$("#addInstanceBtn").click(function() {
		$("#navbarResponsive").removeClass("show");
	    $("#selectInstances").invisible();
      	$("#mainFrame").load("add-instances.html");
    });

    $("#switcherBtn").click(function() {
		$("#navbarResponsive").removeClass("show");
        $("#selectInstances").invisible();
      	$("#mainFrame").load("switch-backend.html");
    });

	$("#logoutBtn").click(function() {
		$("#navbarResponsive").removeClass("show");
	    $("#selectInstances").visible();
		$.ajax({
			url : frontendServiceUrl + "/auth/logout",
			type : "GET",
			contentType : 'application/json; charset=utf-8',
			cache: false,
			complete : function (XMLHttpRequest, textStatus) {
				doIfUserLoggedOut();
				loadMainPage();
			}
		});
	});

	$("#subscriptionBtn").click(function() {
		loadMainPage();
	});

	$("#jmesPathRulesSetUpBtn").click(function() {
		$("#navbarResponsive").removeClass("show");
	    $("#selectInstances").visible();
		$("#mainFrame").load("jmesPathRulesSetUp.html");
	});

	function doIfUserLoggedOut() {
		localStorage.removeItem("currentUser");
		$("#userName").text("Guest");
		$("#loginBlock").show();
		$("#logoutBlock").hide();
		localStorage.setItem('errorsStore', []);
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

	var initOneTime = function(){
		initOneTime = function(){}; // kill it as soon as it was called
		loadDocumentLinks();
		loadMainPage();
	};

	initOneTime();
});