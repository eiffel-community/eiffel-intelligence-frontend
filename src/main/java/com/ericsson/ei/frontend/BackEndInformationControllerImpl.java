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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
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
                instance.addProperty("default", false);
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

    public ResponseEntity<String> switchBackEndInstanceByMainPage(Model model, HttpServletRequest request) {
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            List<BackEndInformation> info = new ArrayList<>();
            for (BackEndInformation backEndInformation : utils.getInformation()) {
                backEndInformation.setActive(false);
                if (backEndInformation.getName().equals(body)) {
                    utils.setBackEndProperties(backEndInformation);
                    backEndInformation.setActive(true);
                }
                info.add(backEndInformation);
            }
            utils.setInformation(info);
            JsonArray result = (JsonArray) new Gson().toJsonTree(utils.getInformation(), new TypeToken<List<BackEndInformation>>() {
            }.getType());
            utils.setInstances(result);
            utils.writeIntoFile();
            utils.parseBackEndInstancesFile();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
