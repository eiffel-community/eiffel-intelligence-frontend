
jQuery(document).ready(function() {

	// Fetch injected URL from DOM
	var eiffelDocumentationUrlLinks = $('#eiffelDocumentationUrlLinks').text();
	var frontendServiceUrl = $('#frontendServiceUrl').text();

	function loadMainPage() {
		$("#mainFrame").load("subscriptionpage.html");
	}

	$("#testRulesBtn").click(function() {
		$("#mainFrame").load("testRules.html");
	});

	$("#eiInfoBtn").click(function() {
		$("#mainFrame").load("eiInfo.html");
	});

	$("#loginBtn").click(function() {
		$("#mainFrame").load("login.html");
	});

	$("#logoutBtn").click(function() {
		$.ajax({
			url : "/auth/logout",
			type : "GET",
			contentType : 'application/json; charset=utf-8',
			cache: false,
			complete : function (XMLHttpRequest, textStatus) {
				localStorage.removeItem("currentUser");
				$("#userName").text("Guest");
				$("#loginBlock").show();
				$("#logoutBlock").hide();
				loadMainPage();
			}
		});
	});

	$("#subscriptionBtn").click(function() {
		loadMainPage();
	});

	$("#jmesPathRulesSetUpBtn").click(function() {
		$("#mainFrame").load("jmesPathRulesSetUp.html");
	});

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

	var currentUser = localStorage.getItem("currentUser");
	if(currentUser != "") {
		$("#userName").text(currentUser);
		$("#logoutBlock").show();
	}
});