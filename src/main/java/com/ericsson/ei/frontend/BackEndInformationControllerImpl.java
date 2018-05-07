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

import com.ericsson.ei.frontend.model.BackEndInformation;
import com.ericsson.ei.frontend.utils.BackEndInstancesUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

@Component
public class BackEndInformationControllerImpl implements BackEndInformationController {

    @Autowired
    private BackEndInformation backEndInformation;

    @Autowired
    private BackEndInstancesUtils utils;

    public ResponseEntity<String> getInstances(Model model) {
        return new ResponseEntity<>(utils.getInstances().toString(), HttpStatus.OK);
    }

    public ResponseEntity<String> switchBackEndInstance(Model model, HttpServletRequest request) {
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            utils.setInstances(new JsonParser().parse(body).getAsJsonArray());
            utils.writeIntoFile();
            utils.parseBackEndInstancesFile();
            for (BackEndInformation backEndInformation : utils.getInformation()) {
                if (backEndInformation.isActive()) {
                    utils.setBackEndProperties(backEndInformation);
                }
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> deleteBackEndInstance(Model model, HttpServletRequest request) {
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            utils.setInstances(new JsonParser().parse(body).getAsJsonArray());
            utils.writeIntoFile();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> addInstanceInformation(Model model, HttpServletRequest request) {
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            JsonObject instance = new JsonParser().parse(body).getAsJsonObject();
            if (!utils.checkIfInstanceAlreadyExist(instance)) {
                instance.addProperty("checked", false);
                utils.getInstances().add(instance);
                utils.writeIntoFile();
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Instance already exist", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
