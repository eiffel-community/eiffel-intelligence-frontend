
jQuery(document).ready(function() {
	

	document.getElementById("subscriptionBtn").onclick = function() {

    	var iframe = document.getElementById("mainFrame");
    	iframe.src = "http://localhost:8080/subscriptionpage";    
	}
	
	document.getElementById("testRulesBtn").onclick = function() {		  
		
    	var iframe = document.getElementById("mainFrame");
    	iframe.src = "http://localhost:8080/testrules";    
	}

});