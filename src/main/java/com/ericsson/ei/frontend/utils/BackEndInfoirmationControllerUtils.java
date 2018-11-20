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
package com.ericsson.ei.frontend.utils;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component
public class BackEndInfoirmationControllerUtils {

    private static final Logger LOG = LoggerFactory.getLogger(BackEndInfoirmationControllerUtils.class);

    @Autowired
    private BackEndInstancesUtils backEndInstancesUtils;

    /**
     * Processes a request to get all available back ends and returns
     * data containing all back end instances with added information
     * weather a back end is set as active or not.
     *
     * @param request
     * @return new ResponseEntity
     */
    public ResponseEntity<String> handleRequestForInstances(HttpServletRequest request) {
        try {
            String activeInstance = null;
            if (request.getSession().getAttribute("backEndInstanceName") != null) {
                activeInstance = request.getSession().getAttribute("backEndInstanceName").toString();
            }

            JsonArray allAvailableInstances = backEndInstancesUtils.getBackEndsAsJsonArray();
            allAvailableInstances = setActiveInstance(allAvailableInstances, activeInstance);

            return new ResponseEntity<>(
                    allAvailableInstances.toString(),
                    getHeaders(), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("ERROR!\n" + e.getMessage());
            return new ResponseEntity<>(
                    "[{\"name\":\"Unable to load instances\",\"host\":\"NO HOST\",\"port\":\"NO PORT\",\"path\":\"/\"}]",
                    getHeaders(), HttpStatus.OK);
        }
    }

    /**
     * Processes requests to switch back end, and sets the back end name to a session variable that is
     * kept for current session.
     *
     * @param request
     * @return new ResponseEntity
     */
    public ResponseEntity<String> handleRequestToSwitchBackEnd(HttpServletRequest request) {
        try {
            String selectedInstanceName = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            request.getSession().setAttribute("backEndInstanceName", selectedInstanceName);

            return new ResponseEntity<>(
                    "{\"message\": \"Backend instance with name '" + selectedInstanceName + "' was selected.\"}",
                    getHeaders(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Internal error" + e.getMessage(),
                    getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Processes requests made to delete back end and deletes
     * the given jsonobject from the back end list and file.
     *
     * @param request
     * @return new ResponseEntity
     */
    public ResponseEntity<String> handleRequestToDeleteBackEnd(HttpServletRequest request) {
        try {
            String instanceAsString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            JsonObject objectToDelete = new JsonParser().parse(instanceAsString).getAsJsonObject();

            LOG.debug("Object recieved to delete: " + objectToDelete);

            backEndInstancesUtils.deleteBackEnd(objectToDelete);
            return new ResponseEntity<>(
                    "{\"message\": \"Backend instance with name '"
                    + objectToDelete.get("name").getAsString() + "' was deleted.\"}",
                    getHeaders(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Internal error" + e.getMessage(),
                    getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Processes requests to add back end, gets data as String formatted JsonObject
     * and adds the date to the back end information if the data is unique.
     *
     * @param request
     * @return new ResponseEntity
     */
    public ResponseEntity<String> handleRequestToAddBackEnd(HttpServletRequest request) {
        try {
            String newInstanceAsString = request.getReader().lines()
                    .collect(Collectors.joining(System.lineSeparator()));
            JsonObject instance = new JsonParser().parse(newInstanceAsString).getAsJsonObject();

            if (backEndInstancesUtils.checkIfInstanceNameAlreadyExist(instance)) {
                LOG.debug("Not a unique name.");
                return new ResponseEntity<>(
                        "{\"message\": \"Backend instance with name '"
                        + instance.get("name").getAsString() + "' already exists.\"}",
                        getHeaders(), HttpStatus.BAD_REQUEST);
            }

            if (!backEndInstancesUtils.checkIfInstanceAlreadyExist(instance)) {
                backEndInstancesUtils.addNewBackEnd(instance);
                LOG.debug("Added new back end.");
                return new ResponseEntity<>(
                        "{\"message\": \"Backend instance with name '"
                        + instance.get("name").getAsString() + "' was added.\"}",
                        getHeaders(), HttpStatus.OK);
            } else {
                LOG.debug("Back end already exist.");
                return new ResponseEntity<>(
                        "{\"message\": \"Backend instance already exist.\"}",
                        getHeaders(), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Internal error, " + e.getMessage(),
                    getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setLocation(URI.create("/"));
        return httpHeaders;
    }

    private JsonArray setActiveInstance(JsonArray allAvailableInstances, String activeInstance) {
        if (allAvailableInstances.size() == 0) {
            throw new NoSuchElementException ("No Available instances.");
        }
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