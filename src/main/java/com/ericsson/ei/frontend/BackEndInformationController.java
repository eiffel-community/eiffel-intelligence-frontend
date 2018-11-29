/*
   Copyright 2018 Ericsson AB.
   For a full list of individual contributors, please see the commit history.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.ericsson.ei.frontend;


import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ericsson.ei.frontend.utils.BackEndInfoirmationControllerUtils;

@Controller
public class BackEndInformationController {

    public static final Logger LOG = LoggerFactory.getLogger(BackEndInformationController.class);

    @Autowired
    private BackEndInfoirmationControllerUtils backEndInfoContUtils;

    @CrossOrigin
    @RequestMapping(value = "/backend", method = RequestMethod.GET)
    public ResponseEntity<String> getInstances(Model model, HttpServletRequest request) {
        LOG.debug("Recieved request for instances.");
        ResponseEntity<String> response = backEndInfoContUtils.handleRequestForInstances(request);
        return response;
    }

    @CrossOrigin
    @RequestMapping(value = "/backend", method = RequestMethod.PUT)
    public ResponseEntity<String> switchBackEndInstance(Model model, HttpServletRequest request) {
        LOG.debug("Recieved request to switch back end.");
        ResponseEntity<String> response  = backEndInfoContUtils.handleRequestToSwitchBackEnd(request);
        return response;

    }

    @CrossOrigin
    @RequestMapping(value = "/backend", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteBackEndInstance(Model model, HttpServletRequest request) {
        LOG.debug("Recieved request to delete back end.");
        ResponseEntity<String> response  = backEndInfoContUtils.handleRequestToDeleteBackEnd(request);
        return response;
    }

    @CrossOrigin
    @RequestMapping(value = "/backend", method = RequestMethod.POST)
    public ResponseEntity<String> addInstanceInformation(Model model, HttpServletRequest request) {
        LOG.debug("Recieved request to add back end.");
        ResponseEntity<String> response  = backEndInfoContUtils.handleRequestToAddBackEnd(request);
        return response;
    }
}
