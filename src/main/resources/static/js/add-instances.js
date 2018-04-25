jQuery(document).ready(function() {
    function loadSwitchPage(){

        $("#mainFrame").load("switch-backend.html");
    }

	document.getElementById("addBtn").onclick = function() {

      	 loadSwitchPage();
    }
});