
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
	
	
	function loadLoginPage() {
 
    	$("#mainFrame").load("login.html");
	}
	
	document.getElementById("loginBtn").onclick = function() {		  
		
		loadLoginPage();
	}
	
	document.getElementById("logoutLoginBtn").onclick = function() {		  
		
		loadLoginPage();
	}
	
	document.getElementById("registerBtn").onclick = function() {		  

    	$("#mainFrame").load("register.html");
	}
	
	document.getElementById("forgotPasswordBtn").onclick = function() {		  

    	$("#mainFrame").load("forgot-password.html");
	}

    function loadAddInstancePage(){

        $("#mainFrame").load("add-instances.html");
    }

    function loadSwitchPage(){

        $("#mainFrame").load("switch-backend.html");
    }

	document.getElementById("addInstanceBtn").onclick = function() {

      	loadAddInstancePage();
    }

    document.getElementById("switcherBtn").onclick = function() {

      	loadSwitchPage();
    }

	function loadMainPage() {

    	$("#mainFrame").load("subscriptionpage.html");
	}
	
	document.getElementById("subscriptionBtn").onclick = function() {
		
    	loadMainPage();
	}

	function loadJmesPathRulesSetUpPage() {

    	$("#mainFrame").load("jmesPathRulesSetUp.html");
	}

	document.getElementById("jmesPathRulesSetUpBtn").onclick = function() {

    	loadJmesPathRulesSetUpPage();
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