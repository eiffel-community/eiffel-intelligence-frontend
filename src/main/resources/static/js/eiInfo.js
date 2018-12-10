jQuery(document).ready(function() {
    // Fetch injected URL from DOM
    var frontEndServiceUrl = $('#frontendServiceUrl').text();
    var frontEndVersion = $('#frontEndVersion').text();
    var frontEndApplicationPropertiesVersion = $('#frontEndApplicationPropertiesVersion').text();
    var frontEndAppName = $('#frontendAppName').text();

    var backEndServerUrl = $('#backendServerUrl').text();

    var body = document.getElementById('eiPageFrame');
    var generalEIInfoLabel = "General Eiffel Intelligence Information";
    var generalEIFrontEndInfoLabel = "General Eiffel Intelligence Front-End Information";

    var defaultEnterpriseVersionName = "Enterprise Version";

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

    function createErrorMessage(inputText) {
        var element = document.createElement('h4');
        element.innerHTML = inputText;
        element.setAttribute('class', 'table-text-setting alert-danger text-center');
        return element;
    }

    function createFrontEndGeneralInfo() {
        if (frontEndApplicationPropertiesVersion) {
            frontEndVersion = frontEndVersion + " (" + frontEndApplicationPropertiesVersion + ")";
        }
        var tableContent = [
            { key: 'Application Name', value: frontEndAppName },
            { key: 'Version', value: frontEndVersion },
            { key: 'EI Front-End URL', value: frontEndServiceUrl }
        ];

        generateGeneralInfo(tableContent, generalEIFrontEndInfoLabel);
    }

    function createGeneralEIInfo(data) {

        if (data.applicationPropertiesVersion) {
            data.version = data.version + " (" + data.applicationPropertiesVersion + ")";
        }

        var tableContent = [
            { key: 'Application Name', value: data.applicationName },
            { key: 'Version', value: data.version },
            { key: 'Rules File Path', value: data.rulesPath },
            { key: 'EI Back-End Connected Server', value: backEndServerUrl },
            { key: 'EI Test Rules functionality enabled', value: data.testRulesEnabled }
        ];

        generateGeneralInfo(tableContent, generalEIInfoLabel);
    }

    function generateGeneralInfo(tableContent, labelText) {
        var tbdy = document.createElement('tbody');

        var label = createLabel(labelText);
        body.appendChild(label);

        var div = document.createElement('div');
        div.setAttribute('class','table-responsive');

        var tbl = createTable();

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
            url: frontEndServiceUrl + "/information",
            contentType : 'application/json;charset=UTF-8',
            type: 'GET',
            error : function (XMLHttpRequest, textStatus, errorThrown) {
                var label = createLabel(generalEIInfoLabel);
                body.appendChild(label);
                var element = createErrorMessage('<strong>Error:</strong> Could not fetch information from back-end!');
                body.appendChild(element);
            },
            success : function (data, textStatus, xhr) {
                var eiInfoContainer = document.getElementById('eiInfoContainer');
                var data = JSON.parse(xhr.responseText);
                createGeneralEIInfo(data);
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

    createFrontEndGeneralInfo();
    getInstanceInfo();

});
