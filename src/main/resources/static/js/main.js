
jQuery(document).ready(function() {
	
	document.getElementById("testRulesBtn").onclick = function() {		  
		
    	var iframe = document.getElementById("mainFrame");
    	iframe.src = "http://localhost:8080/testRules.html";    
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