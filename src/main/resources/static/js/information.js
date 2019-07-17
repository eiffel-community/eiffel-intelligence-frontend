jQuery(document).ready(function () {

    // Fetch injected URL from DOM
    var frontEndServiceUrl = $('#frontendServiceUrl').text();
    var frontEndVersion = $('#frontEndVersion').text();
    var frontEndApplicationPropertiesVersion = $('#frontEndApplicationPropertiesVersion').text();
    var frontEndAppName = $('#frontendAppName').text();

    var body = document.getElementById('eiPageFrame');
    var generalEIInfoLabel = "General Eiffel Intelligence Information";
    var generalEIFrontEndInfoLabel = "General Eiffel Intelligence Front-End Information";

    function createTable() {
        var tbl = document.createElement('table');
        tbl.setAttribute('class', 'table table-bordered table-striped dataTable table-text-setting');
        return tbl;
    }

    function createLabel(inputText) {
        var label = document.createElement('p');
        label.innerHTML = inputText;
        label.setAttribute('class', 'section-p-text table-text-setting font-weight-bold');
        return label;
    }

    function createFrontEndGeneralInfo() {
        if (frontEndApplicationPropertiesVersion) {
            frontEndVersion = frontEndVersion + " (" + frontEndApplicationPropertiesVersion + ")";
        }
        var tableContent = [
            { key: 'Application name', value: frontEndAppName },
            { key: 'Version', value: frontEndVersion },
            { key: 'EI front-end URL', value: frontEndServiceUrl }
        ];

        generateGeneralInfo(tableContent, generalEIFrontEndInfoLabel);
    }

    function createGeneralEIInfo(data) {

        if (data.applicationPropertiesVersion) {
            data.version = data.version + " (" + data.applicationPropertiesVersion + ")";
        }

        var tableContent = [
            { key: 'Application name', value: data.applicationName },
            { key: 'Version', value: data.version },
            { key: 'Rules path', value: data.rulesPath },
            { key: 'EI back-end connected server', value: sessionStorage.getItem(sessionStorage.selectedActive) },
            { key: 'EI test rules functionality enabled', value: data.testRulesEnabled }
        ];

        generateGeneralInfo(tableContent, generalEIInfoLabel);
    }

    function generateGeneralInfo(tableContent, labelText) {
        var tbdy = document.createElement('tbody');

        var label = createLabel(labelText);
        body.appendChild(label);

        var div = document.createElement('div');
        div.setAttribute('class', 'table-responsive');

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
        div.setAttribute('class', 'table-responsive');

        var label = createLabel(tableLabel);
        body.appendChild(label);

        var tbdy = null;
        var tbl = null;

        dataList.forEach(function (dataSubList) {
            tbdy = document.createElement('tbody');
            tbl = createTable();

            Object.keys(dataSubList).forEach(function (dataKey) {
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
        var callback = {
            success: function (responseData, textStatus) {
                createGeneralEIInfo(responseData);
                generateEIInformationBasedOnList(responseData.rabbitmq, "Eiffel Intelligence Connected RabbitMq Instances");
                generateEIInformationBasedOnList(responseData.mongodb, "Eiffel Intelligence Connected MongoDb Instances");
                generateEIInformationBasedOnList(responseData.threads, "Eiffel Intelligence Backend Java Threads Settings");
                generateEIInformationBasedOnList(responseData.email, "Eiffel Intelligence Backend E-Mail Settings");
                generateEIInformationBasedOnList(responseData.mailServerValues, "Eiffel Intelligence Backend SMTP Settings");
                generateEIInformationBasedOnList(responseData.waitList, "Eiffel Intelligence Backend WaitList settings");
                generateEIInformationBasedOnList([responseData.objectHandler], "Eiffel Intelligence Backend ObjectHandler Settings");
                generateEIInformationBasedOnList([responseData.subscriptionHandler], "Eiffel Intelligence Backend SubscriptionHandler Settings");
                generateEIInformationBasedOnList([responseData.informSubscriber], "Eiffel Intelligence Backend InformSubscriber Settings");
                generateEIInformationBasedOnList([responseData.erUrl], "End point for downstream/upstream search in EventRepository");
                generateEIInformationBasedOnList([responseData.ldap], "Eiffel Intelligence Backend LDAP Settings");
            }
        };
        var ajaxHttpSender = new AjaxHttpSender();
        ajaxHttpSender.sendAjax(backendEndpoints.INFORMATION, "GET", null, callback);
    }

    createFrontEndGeneralInfo();
    getInstanceInfo();

    // Check EI Backend Server Status ########################################

    checkBackendStatus();

    // END OF EI Backend Server check #########################################
});
