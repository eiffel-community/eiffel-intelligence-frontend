jQuery(document).ready(function() {
    // Fetch injected URL from DOM
    var frontendServiceUrl = $('#frontendServiceUrl').text();
    var backendServerUrl = $('#backendServerUrl').text();

    var body = document.getElementById('eiPageFrame');

    function createTable() {
        var tbl = document.createElement('table');
        tbl.setAttribute('class', 'table table-bordered table-striped dataTable table-text-setting');
        return tbl;
    }

    function createLabel(inputText) {
        var label = document.createElement('p');
        label.innerHTML = inputText;
        label.setAttribute('class', 'section-p-text table-text-setting');
        return label;
    }

    function generateGeneralEiInfo(data) {
        var tbdy = document.createElement('tbody');

        var label = createLabel('General Eiffel Intelligence Information');
        body.appendChild(label);

        var div = document.createElement('div');
        div.setAttribute('class','table-responsive');

        var tbl = createTable();

        var tableContent = [
            { key: 'Application Name', value: data.applicationName },
            { key: 'Version', value: data.version },
            { key: 'EI Backend Connected Server', value: backendServerUrl },
            { key: 'EI Test Rules functionality enabled', value: data.testRulesEnabled },
        ];

        for (i = 0; i < tableContent.length; i++) {
            key = tableContent[i].key;
            value = tableContent[i].value;
            var tr = createTableRow(key, value);
            tbdy.appendChild(tr);
        }

        tbl.appendChild(tbdy);
        div.appendChild(tbl);
        body.appendChild(div);
    }

    function generateEIInformationBasedOnList(dataList, tableLabel) {
        var div = document.createElement('div');
        div.setAttribute('class','table-responsive');

        var label = createLabel(tableLabel);
        body.appendChild(label);

        var tbdy = null;
        var tbl = null;

        dataList.forEach(function(dataSubList) {
            tbdy = document.createElement('tbody');
            tbl = createTable();

            Object.keys(dataSubList).forEach(function(dataKey) {
                value = dataSubList[dataKey];
                var tr = createTableRow(dataKey, value);
                tbdy.appendChild(tr);
            });

        });

        tbl.appendChild(tbdy);
        div.appendChild(tbl);
        body.appendChild(div);
    }

    function createTableRow(key, value) {
        var tr = document.createElement('tr');
        var tdKey = document.createElement('td');
        tdKey.setAttribute('class', 'left-table-pane');
        tdKey.appendChild(document.createTextNode(key));
        tr.appendChild(tdKey);
        var tdValue = document.createElement('td');
        tdValue.appendChild(document.createTextNode(value));
        tr.appendChild(tdValue);
        return tr;
    }

    function getInstanceInfo() {
        $.ajax({
            url: frontendServiceUrl + "/information",
            contentType : 'application/json;charset=UTF-8',
            type: 'GET',
            error : function (XMLHttpRequest, textStatus, errorThrown) {
                if (XMLHttpRequest.responseText == "") {
                    document.getElementById("eiPageFrame").innerHTML = "<h3 style=\"text-align: center;\">There is no response from backend</h3>";
                } else {
                    document.getElementById("eiPageFrame").innerHTML = "<h3 style=\"text-align: center;\">" + XMLHttpRequest.responseText + "</h3>";
                }
            },
            success : function (data, textStatus, xhr) {
                var eiInfoContainer = document.getElementById('eiInfoContainer');
                var data = JSON.parse(xhr.responseText);
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
                generateEIInformationBasedOnList([data.erUrl], "End point for downstream/upstream search in EventRepository");
                generateEIInformationBasedOnList([data.ldap], "Eiffel Intelligence Backend LDAP Settings");
            },
            complete: function (XMLHttpRequest, textStatus) {}
        });
    }

    getInstanceInfo();

});
