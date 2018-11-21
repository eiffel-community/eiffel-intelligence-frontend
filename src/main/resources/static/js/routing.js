var router = new Navigo(null, true, '#!');

router.on({
    'subscriptions': function () {
        $("#mainFrame").load("subscriptionpage.html");
    },
    'test-rules': function () {
        $("#mainFrame").load("testRules.html");
    },
    'ei-info': function () {
        $("#mainFrame").load("eiInfo.html");
    },
    'switch-backend': function () {
        $("#mainFrame").load("switch-backend.html");
    },
    'add-backend': function () {
        $("#mainFrame").load("add-instances.html");
    },
    'login': function () {
        $("#mainFrame").load("login.html");
    },
    '*': function () {
        $("#mainFrame").load("subscriptionpage.html"); 
    }
}).resolve();