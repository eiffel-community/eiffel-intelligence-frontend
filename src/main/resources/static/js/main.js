
jQuery(document).ready(function() {
	
    // Fetch injected URL from DOM
    var eiffelDocumentationUrlLinks = $('#eiffelDocumentationUrlLinks').text();
	var frontendServiceUrl = $('#frontendServiceUrl').text();

	document.getElementById("testRulesBtn").onclick = function() {		  
		
    	var iframe = document.getElementById("mainFrame");
    	iframe.src = frontendServiceUrl + "/testRules.html";    
	}
	
	document.getElementById("eiInfoBtn").onclick = function() {		  
		
    	var iframe = document.getElementById("mainFrame");
    	iframe.src = frontendServiceUrl + "/eiInfo.html";    
	}
	
	
	function loadLoginPage() {
    	var iframe = document.getElementById("mainFrame");
    	iframe.src = frontendServiceUrl + "/login.html";  
	}
	
	document.getElementById("loginBtn").onclick = function() {		  
		
		loadLoginPage();
	}
	
	document.getElementById("logoutLoginBtn").onclick = function() {		  
		
		loadLoginPage();
	}
	
	document.getElementById("registerBtn").onclick = function() {		  
		
    	var iframe = document.getElementById("mainFrame");
    	iframe.src = frontendServiceUrl + "/register.html";
	}
	
	document.getElementById("forgotPasswordBtn").onclick = function() {		  
		
    	var iframe = document.getElementById("mainFrame");
    	iframe.src = frontendServiceUrl + "/forgot-password.html";
	}
	
	function loadMainPage() {
		var iframe = document.getElementById("mainFrame");
    	iframe.src = frontendServiceUrl + "/subscriptionpage.html";
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