
jQuery(document).ready(function() {
	
    // Fetch injected URL from DOM 
    var eiffelDocumentationUrlLinks = $('#eiffelDocumentationUrlLinks').text();
	var frontendServiceUrl = $('#frontendServiceUrl').text();

	document.getElementById("testRulesBtn").onclick = function() {		  

    	$("#mainFrame").load("testRules.html");
	}
	
	document.getElementById("eiInfoBtn").onclick = function() {		  
  
    	$("#mainFrame").load("eiInfo.html");
	}
	
	document.getElementById("loginBtn").onclick = function() {		  
		
		$("#mainFrame").load("login.html");
	}
	
	document.getElementById("logoutBtn").onclick = function() {
		$.ajax({
		    url : "/auth/logout",
		    type : "GET",
		    contentType : 'application/json; charset=utf-8',
		    cache: false,
		    complete : function (XMLHttpRequest, textStatus) {
		        loadMainPage();
		    }
		});
	}
	
	document.getElementById("registerBtn").onclick = function() {		  

    	$("#mainFrame").load("register.html");
	}
	
	document.getElementById("forgotPasswordBtn").onclick = function() {		  

    	$("#mainFrame").load("forgot-password.html");
	}
	
	function loadMainPage() {

    	$("#mainFrame").load("subscriptionpage.html");
	}
	
	document.getElementById("subscriptionBtn").onclick = function() {
		
    	loadMainPage();
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