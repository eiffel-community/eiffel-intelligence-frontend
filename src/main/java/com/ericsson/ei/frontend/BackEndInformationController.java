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

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public interface BackEndInformationController {

    @RequestMapping(value = "/get-instances", method = RequestMethod.GET)
    ResponseEntity<String> getInstances(Model model);

    @RequestMapping(value = "/switch-backend", method = RequestMethod.POST)
    ResponseEntity<String> switchBackEndInstance(Model model, HttpServletRequest request);

    @RequestMapping(value = "/switch-backend", method = RequestMethod.DELETE)
    ResponseEntity<String> deleteBackEndInstance(Model model, HttpServletRequest request);

    @RequestMapping(value = "/add-instances", method = RequestMethod.POST)
    ResponseEntity<String> addInstanceInformation(Model model, HttpServletRequest request);
}
