var template_vars = {};


//default subscription object (Add subscription)

    var default_json_empty =

           [{
            "created": "",
            "notificationMeta" : "",
            "notificationType" : "",
            "restPostBodyMediaType" : "",
            "notificationMessageKeyValues" : [
                    {
                        "formkey" : "",
                        "formvalue" : ""
                    }
                ],
            "notificationMessageKeyValuesAuth" : [
                    {
                        "formkey" : "",
                        "formvalue" : ""
                    }
                ],
            "repeat" : null,
            "requirements" : [
                {
                    "conditions" : [
                        {
                            "jmespath" : ""
                        }
                    ],
                }
            ],
            "subscriptionName" : ""
        }];


// Subscription Template Jenkins Pipeline Parameterized Job trigger
template_vars["templatejenkinsPipelineParameterizedBuildTrigger"] =
                [{
                    "created": "",
                    "notificationMeta" : "http://<JenkinsHost:port>/job/<JobName>/job/<branch>/build",
                    "notificationType" : "REST_POST",
                    "restPostBodyMediaType" : "application/x-www-form-urlencoded",
                    "notificationMessageKeyValues" : [
                    {
                        "formkey" : "json",
                        "formvalue" : "{parameter: [{ name: 'jsonparams', value : to_string(@) }, { name: 'runpipeline', value : 'mybuildstep' }]}"
                    }
                    ],
                    "notificationMessageKeyValuesAuth" : [
                    {
                        "formkey" : "myAuthentication",
                        "formvalue" : "myToken"
                    }
                ],
                    "repeat" : false,
                    "requirements" : [
                    {
                        "conditions" : [
                            {
                                "jmespath" : "submission.sourceChanges[?submitter.group == 'Team Gophers' && svnIdentifier==null]"
                            }
                        ]
                    }
                ],
                    "subscriptionName" : "<Subscription_Template_Jenkins_Pipline_Trigger>"
                }];


// Subscription Template REST POST RAW BODY JSON Trigger
template_vars["templateRestPostJsonRAWBodyTrigger"] =
                [{
                    "created": "",
                    "notificationMeta" : "http://<MyHost:port>/api/doit",
                    "notificationType" : "REST_POST",
                    "restPostBodyMediaType" : "application/json",
                    "notificationMessageKeyValues" : [
                    {
                        "formkey" : "",
                        "formvalue" : "{mydata: [{ fullaggregation : to_string(@) }]}"
                    }
                ],
                    "repeat" : false,
                    "requirements" : [
                    {
                        "conditions" : [
                            {
                                "jmespath" : "submission.sourceChanges[?submitter.group == 'Team Gophers' && svnIdentifier==null]"
                            }
                        ]
                    }
                ],
                    "subscriptionName" : "<Subscription_Template_Rest_Post_Raw_Body_Json_Trigger>"
                }];


// Subscription Template MAIL Trigger
template_vars["templateEmailTrigger"] =
                [{
                    "created": "",
                    "notificationMeta" : "mymail@company.com",
                    "notificationType" : "MAIL",
                    "restPostBodyMediaType" : "",
                    "notificationMessageKeyValues" : [
                    {
                        "formkey" : "",
                        "formvalue" : "{mydata: [{ fullaggregation : to_string(@) }]}"
                    }
                ],
                    "repeat" : false,
                    "requirements" : [
                    {
                        "conditions" : [
                            {
                                "jmespath" : "submission.sourceChanges[?submitter.group == 'Team Gophers' && svnIdentifier==null]"
                            }
                        ]
                    }
                ],
                    "subscriptionName" : "<Subscription_Template_Mail_Trigger>"
                }];