jQuery(document).ready(function() {
    function loadSwitchPage(){

        $("#mainFrame").load("switch-backend.html");
    }

	document.getElementById("deleteBtn").onclick = function() {

      	 loadSwitchPage();
    }
var sendbtn = document.getElementById('switcher').disabled = true;
$('input[type="checkbox"]').on('change', function() {
    var sendbtn = document.getElementById('switcher').disabled = false;
    $('input[name="' + this.name + '"]').not(this).prop('checked', false);
});
});