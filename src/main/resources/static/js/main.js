
jQuery(document).ready(function() {
	
	document.getElementById("testRulesBtn").onclick = function() {		  
		
    	var iframe = document.getElementById("mainFrame");
    	iframe.src = "http://localhost:8080/testRules.html";    
	}
	
	document.getElementById("eiInfoBtn").onclick = function() {		  
		
    	var iframe = document.getElementById("mainFrame");
    	iframe.src = "http://localhost:8080/eiInfo.html";    
	}
	
	
	function loadLoginPage() {
    	var iframe = document.getElementById("mainFrame");
    	iframe.src = "http://localhost:8080/login.html";  
	}
	
	document.getElementById("loginBtn").onclick = function() {		  
		
		loadLoginPage();
	}
	
	document.getElementById("logoutLoginBtn").onclick = function() {		  
		
		loadLoginPage();
	}
	
	document.getElementById("registerBtn").onclick = function() {		  
		
    	var iframe = document.getElementById("mainFrame");
    	iframe.src = "http://localhost:8080/register.html";
	}
	
	document.getElementById("forgotPasswordBtn").onclick = function() {		  
		
    	var iframe = document.getElementById("mainFrame");
    	iframe.src = "http://localhost:8080/forgot-password.html";
	}
	
	function loadMainPage() {
		var iframe = document.getElementById("mainFrame");
    	iframe.src = "http://localhost:8080/subscriptionpage.html";
	}
	
	document.getElementById("subscriptionBtn").onclick = function() {
		
    	loadMainPage();    
	}
	
	var initOneTime = function(){
		initOneTime = function(){}; // kill it as soon as it was called
	     loadMainPage();
	};
	
	initOneTime();
});