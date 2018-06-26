


jQuery(document).ready(function() {
	
    // Fetch injected URL from DOM
    var frontendServiceUrl = $('#frontendServiceUrl').text();
    var backendServerUrl = $('#backendServerUrl').text();
    
    var tableTdKeyWidth = '250';

    var body = document.getElementById('eiPageFrame');
    
    function createTable() {
        var tbl = document.createElement('table');
        tbl.style.width = '40%';
        tbl.setAttribute('border', '3');
        tbl.setAttribute('align', 'center');
        tbl.setAttribute('class', 'table table-bordered table-striped dataTable');
        return tbl;
    }
	
    // Fetch injected URL from DOM
    frontendServiceUrl = $('#frontendServiceUrl').text();

	function generateGeneralEiInfo(data) {
		    
        body.appendChild(document.createElement('br'));
        var tbdy = document.createElement('tbody');

        var label = document.createElement('p');
        label.innerHTML = 'General Eiffel Intelligence Information';
        label.setAttribute('align', 'center');
        body.appendChild(label);
		
        var tbl = createTable();

        var tr = document.createElement('tr');
        var tdKey = document.createElement('td');
        tdKey.setAttribute('width', tableTdKeyWidth);
        tdKey.appendChild(document.createTextNode('ApplicationName'));
        tr.appendChild(tdKey);
        var tdValue = document.createElement('td');
//        tdValue.setAttribute('width', '30px');
        tdValue.appendChild(document.createTextNode(data.applicationName));
        tr.appendChild(tdValue);
        tbdy.appendChild(tr);

        
        tr = document.createElement('tr');
        tdKey = document.createElement('td');
        tdKey.setAttribute('width', tableTdKeyWidth);
        tdKey.appendChild(document.createTextNode('Version'));
        tr.appendChild(tdKey);
        tdValue = document.createElement('td');
        tdValue.appendChild(document.createTextNode(data.version));
        tr.appendChild(tdValue);
        tbdy.appendChild(tr);
        
        tr = document.createElement('tr');
        tdKey = document.createElement('td');
        tdKey.setAttribute('width', tableTdKeyWidth);
        tdKey.appendChild(document.createTextNode('EI RestApi Requests Via Server'));
        tr.appendChild(tdKey);
        tdValue = document.createElement('td');
        tdValue.appendChild(document.createTextNode(frontendServiceUrl));
        tr.appendChild(tdValue);
        tbdy.appendChild(tr);
        
        tr = document.createElement('tr');
        tdKey = document.createElement('td');
        tdKey.setAttribute('width', tableTdKeyWidth);
        tdKey.appendChild(document.createTextNode('EI Backend Connected Server'));
        tr.appendChild(tdKey);
        tdValue = document.createElement('td');
        tdValue.appendChild(document.createTextNode(backendServerUrl));
        tr.appendChild(tdValue);
        tbdy.appendChild(tr);
        
        tr = document.createElement('tr');
        tdKey = document.createElement('td');
        tdKey.setAttribute('width', tableTdKeyWidth);
        tdKey.appendChild(document.createTextNode('EI Test Rules functionality enabled'));
        tr.appendChild(tdKey);
        tdValue = document.createElement('td');
        tdValue.appendChild(document.createTextNode(data.testRulesEnabled));
        tr.appendChild(tdValue);
        tbdy.appendChild(tr);
        
        
        tbl.appendChild(tbdy);
        body.appendChild(tbl);
        
        body.appendChild(document.createElement('br'));
	}
	
	function generateEIInformationBasedOnList(dataList, tableLabel) {
		        
        var label = document.createElement('p');
        label.innerHTML = tableLabel;
        label.setAttribute('align', 'center');
        body.appendChild(label);
        
        var tbdy = null;
        var tbl = null;
        
        dataList.forEach(function(dataSubList) {
        		tbdy = document.createElement('tbody');
        		tbl = createTable();

        	    Object.keys(dataSubList).forEach(function(dataKey) {
        	        var tr = document.createElement('tr');
        	        var tdKey = document.createElement('td');
        	        tdKey.setAttribute('width', tableTdKeyWidth);
        	        tdKey.appendChild(document.createTextNode(dataKey));
        	        tr.appendChild(tdKey);
        	        var tdValue = document.createElement('td');
        	        tdValue.appendChild(document.createTextNode(dataSubList[dataKey]));
        	        tr.appendChild(tdValue);
        	        tbdy.appendChild(tr);
        	    	
        	    });
        	    
        });
		
        tbl.appendChild(tbdy);
        body.appendChild(tbl);
        
        body.appendChild(document.createElement('br'));
        
	}

    function getInstanceInfo() {
        var eiInfoContainer = document.getElementById('eiInfoContainer');
        $.ajax({
              url: frontendServiceUrl + "/information",
              contentType : 'application/json;charset=UTF-8',
              type: 'GET',
              error : function (XMLHttpRequest, textStatus, errorThrown) {
                      document.getElementById('info_text').innerHTML = errorThrown;
              },
              success : function (data, textStatus, xhr) {

              },
        	  complete: function (XMLHttpRequest, textStatus) {
        	            var data = JSON.parse(XMLHttpRequest.responseText);

        	            generateGeneralEiInfo(data);
        	            
        	            generateEIInformationBasedOnList(data.rabbitmq, "Eiffel Intelligence Connected RabbitMq Instances");
        	            generateEIInformationBasedOnList(data.mongodb, "Eiffel Intelligence Connected MongoDb Instances");
        	            generateEIInformationBasedOnList(data.threads, "Eiffel Intelligence Backend Java Threads Settings");
        	            generateEIInformationBasedOnList(data.email, "Eiffel Intelligence Backend E-Mail Settings");
        	            generateEIInformationBasedOnList(data.mailServerValues, "Eiffel Intelligence Backend SMTP Settings");
        	            generateEIInformationBasedOnList(data.waitList, "Eiffel Intelligence Backend WaitList settings");
        	            generateEIInformationBasedOnList([data.objectHandler], "Eiffel Intelligence Backend ObjectHandler Settings");
        	            generateEIInformationBasedOnList([data.subscriptionHandler], "Eiffel Intelligence Backend SubscriptionHandler Settings");
        	            generateEIInformationBasedOnList([data.informSubscription], "Eiffel Intelligence Backend InformSubscription Settings");
        	            generateEIInformationBasedOnList([data.erUrl], "Eiffel Intelligence Backend EventRepository Url");
        	            generateEIInformationBasedOnList([data.ldap], "Eiffel Intelligence Backend LDAP Settings");
        	  		}
        	  });

    }
	
    getInstanceInfo();

});