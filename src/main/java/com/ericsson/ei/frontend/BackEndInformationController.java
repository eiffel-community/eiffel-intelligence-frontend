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

import java.io.IOException;
import java.net.URI;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ericsson.ei.frontend.utils.BackEndInstancesUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Controller
public class BackEndInformationController {

    public static final Logger LOG = LoggerFactory.getLogger(BackEndInformationController.class);

    @Autowired
    private BackEndInstancesUtils backEndInstancesUtils;

    @RequestMapping(value = "/get-instances", method = RequestMethod.GET)
    public ResponseEntity<String> getInstances(Model model, HttpServletRequest request) {
        LOG.debug("Recieved request for instances.");
        try {
            String activeInstance = null;
            JsonArray allAvailableInstances = backEndInstancesUtils.getBackEndsAsJsonArray();

            if (request.getSession().getAttribute("backEndInstanceName") != null) {
                activeInstance = request.getSession().getAttribute("backEndInstanceName").toString();
            }

            allAvailableInstances = setActiveInstance(allAvailableInstances, activeInstance);

            return new ResponseEntity<>(allAvailableInstances.toString(), getHeaders(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("[{\"name\":\"Unable to load instances\",\"host\":\"NO HOST\",\"port\":\"NO PORT\",\"path\":\"/\"}]", getHeaders(), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/switch-backend", method = RequestMethod.POST)
    public ResponseEntity<String> switchBackEndInstance(Model model, HttpServletRequest request) {
        LOG.debug("Recieved request to switch back end.");
        try {
            String selectedInstanceName = getSelectedInstanceName(request);

            request.getSession().setAttribute("backEndInstanceName", selectedInstanceName);

            LOG.error("Session name: " + String.valueOf(request.getSession().getAttribute("backEndInstanceName")));

            return new ResponseEntity<>(getHeaders(), HttpStatus.MOVED_PERMANENTLY);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal error" + e.getMessage(), getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/switch-backend", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteBackEndInstance(Model model, HttpServletRequest request) {
        LOG.debug("Recieved request to delete back end.");
        try {
            String instanceAsString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            JsonObject objectToDelete = new JsonParser().parse(instanceAsString).getAsJsonObject();
            LOG.info("Input: " + objectToDelete);

            backEndInstancesUtils.deleteBackEnd(objectToDelete);
            return new ResponseEntity<>("Backend instance was deleted", getHeaders(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal error" + e.getMessage(), getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/add-instances", method = RequestMethod.POST)
    public ResponseEntity<String> addInstanceInformation(Model model, HttpServletRequest request) {
        LOG.debug("Recieved request to add instance.");
        try {
            String newInstanceAsString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            JsonObject instance = new JsonParser().parse(newInstanceAsString).getAsJsonObject();

            if (backEndInstancesUtils.checkIfInstanceNameAlreadyExist(instance)) {
                return new ResponseEntity<>("Instance name must be unique", getHeaders(), HttpStatus.BAD_REQUEST);
            }

            if (!backEndInstancesUtils.checkIfInstanceAlreadyExist(instance)) {
                backEndInstancesUtils.addNewBackEnd(instance);
                return new ResponseEntity<>(getHeaders(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Instance already exist", getHeaders(), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Internal error" + e.getMessage(), getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setLocation(URI.create("/"));
        return httpHeaders;
    }

    private String getSelectedInstanceName(HttpServletRequest request) throws IOException {
        String selectedInstanceName = null;
        String inputPostDataAsString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        if (isJsonFormatted(inputPostDataAsString)) {
            JsonArray listofInstances = new JsonParser().parse(inputPostDataAsString).getAsJsonArray();

            for (JsonElement element : listofInstances) {
                if (element.getAsJsonObject().get("active").getAsBoolean() == true) {
                    selectedInstanceName = element.getAsJsonObject().get("name").getAsString();
                    break;
                }
            }
        } else {
            selectedInstanceName = inputPostDataAsString;
        }

        return selectedInstanceName;
    }

    private boolean isJsonFormatted(String listOfInstances) {
        try {
            new JsonParser().parse(listOfInstances).getAsJsonArray();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private JsonArray setActiveInstance(JsonArray allAvailableInstances, String activeInstance) {
        for (JsonElement element : allAvailableInstances) {
            if (activeInstance == null && element.getAsJsonObject().get("defaultBackend").getAsBoolean()) {
                element.getAsJsonObject().addProperty("active", true);
            } else if (activeInstance != null
                    && element.getAsJsonObject().get("name").getAsString().equals(activeInstance)) {
                element.getAsJsonObject().addProperty("active", true);
            } else {
                element.getAsJsonObject().addProperty("active", false);
            }
        }
        return allAvailableInstances;
    }
}
