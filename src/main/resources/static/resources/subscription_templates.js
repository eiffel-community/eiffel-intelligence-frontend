var template_vars = {};


//default subscription object (Add subscription)

    var default_json_empty =

           [{
            "created": "",
            "notificationMeta" : "",
            "notificationType" : "",
            "restPostBodyMediaType" : "",
            "notificationMessageRawJson" : "",
            "notificationMessageKeyValues" : [
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
                    "notificationMessageRawJson" : "",
                    "notificationMessageKeyValues" : [
                    {
                        "formkey" : "json",
                        "formvalue" : "{parameter: [{ name: 'jsonparams', value : to_string(@) }, { name: 'runpipeline', value : 'mybuildstep' }]}"
                    }                    
                    ],                    
                    "repeat" : false,
                    "requirements" : [
                    {
                        "conditions" : [
                            {
                                "jmespath" : "submission.sourceChanges[?submitter.group == 'Team Gophers' && gitIdentifier==null]"
                            }
                        ]
                    }
                ],
                    "subscriptionName" : "Subscription_Template_Jenkins_Pipline_Trigger"
                }];


// Subscription Template REST POST RAW BODY JSON Trigger
template_vars["templateRestPostJsonRAWBodyTrigger"] =
                [{
                    "created": "",
                    "notificationMeta" : "http://<MyHost:port>/api/doit",
                    "notificationType" : "REST_POST",
                    "restPostBodyMediaType" : "application/json",
                    "notificationMessageRawJson" : "{mydata: [{ fullaggregation : to_string(@) }]}",
                    "notificationMessageKeyValues" : [
                    {
                        "formkey" : "",
                        "formvalue" : ""
                    }
                ],
                    "repeat" : false,
                    "requirements" : [
                    {
                        "conditions" : [
                            {
                                "jmespath" : "submission.sourceChanges[?submitter.group == 'Team Gophers' && gitIdentifier==null]"
                            }
                        ]
                    }
                ],
                    "subscriptionName" : "Subscription_Template_Rest_Post_Raw_Body_Json_Trigger"
                }];


// Subscription Template MAIL Trigger
template_vars["templateEmailTrigger"] =
                [{
                    "created": "",
                    "notificationMeta" : "mymail@company.com",
                    "notificationType" : "MAIL",
                    "restPostBodyMediaType" : "",
                    "notificationMessageRawJson" : "{mydata: [{ fullaggregation : to_string(@) }]}",
                    "notificationMessageKeyValues" : [
                    {
                        "formkey" : "",
                        "formvalue" : ""
                    }
                ],
                    "repeat" : false,
                    "requirements" : [
                    {
                        "conditions" : [
                            {
                                "jmespath" : "submission.sourceChanges[?submitter.group == 'Team Gophers' && gitIdentifier==null]"
                            }
                        ]
                    }
                ],
                    "subscriptionName" : "Subscription_Template_Mail_Trigger"
                }];
