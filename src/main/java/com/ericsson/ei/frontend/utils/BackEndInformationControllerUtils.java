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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component
public class BackEndInformationControllerUtils {

    private static final Logger LOG = LoggerFactory.getLogger(BackEndInformationControllerUtils.class);

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
            JsonArray allAvailableInstances = backEndInstancesUtils.getBackEndsAsJsonArray();

            return new ResponseEntity<>(
                    allAvailableInstances.toString(),
                    getHeaders(), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("ERROR!\n" + e.getMessage());
            return new ResponseEntity<>(
                    "{\"message\": \"Failure when trying to load backend instances\"}",
                    getHeaders(), HttpStatus.NOT_FOUND);
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
            LOG.error("Error while switching instance: " + e.getMessage());
            String response = "{\"message\": \"Internal Error: " + e.getMessage() + "\"}";
            return new ResponseEntity<>(response, getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
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
            LOG.error("Error while deleting instance: " + e.getMessage());
            String response = "{\"message\": \"Internal Error: " + e.getMessage() + "\"}";
            return new ResponseEntity<>(response, getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
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

            final boolean hasRequiredData = backEndInstancesUtils.hasRequiredJsonKeys(instance);
            if (!hasRequiredData) {
                LOG.debug("Json data is missing required keys");
                return new ResponseEntity<>(
                        "{\"message\": \"Back-end instance is missing required data.\"}",
                        getHeaders(), HttpStatus.BAD_REQUEST);
            }

            final boolean hasNullValues = backEndInstancesUtils.containsNullValues(instance);
            if (hasNullValues) {
                LOG.debug("Json data contains null values");
                return new ResponseEntity<>(
                        "{\"message\": \"Back-end instance can not have null values.\"}",
                        getHeaders(), HttpStatus.BAD_REQUEST);
            }

            final boolean containsUnrecognizedKeys = backEndInstancesUtils.containsAdditionalKeys(instance);
            if (containsUnrecognizedKeys) {
                LOG.debug("JSON data contains unrecognized keys");
                return new ResponseEntity<>(
                        "{\"message\": \"Back-end instance contains unrecognized JSON keys.\"}",
                        getHeaders(), HttpStatus.BAD_REQUEST);
            }

            final boolean instanceNameAlreadyExist = backEndInstancesUtils.checkIfInstanceNameAlreadyExist(instance);
            if (instanceNameAlreadyExist) {
                LOG.debug("Not a unique name.");
                return new ResponseEntity<>(
                        "{\"message\": \"Back-end instance with name '"
                        + instance.get("name").getAsString() + "' already exists.\"}",
                        getHeaders(), HttpStatus.BAD_REQUEST);
            }

            final boolean instanceHasDefaultFlag = instance.has(BackEndInstancesUtils.DEFAULT);
            if(instanceHasDefaultFlag && instance.get(BackEndInstancesUtils.DEFAULT).getAsBoolean()) {
                final boolean defaultBackEndExist = backEndInstancesUtils.hasDefaultBackend();
                if (defaultBackEndExist) {
                    LOG.debug("Default back-end instance already exists.");
                    return new ResponseEntity<>(
                            "{\"message\": \"A default back-end instance already exists.\"}",
                            getHeaders(), HttpStatus.BAD_REQUEST);
                }
            }

            final boolean instanceValuesAlreadyExist = backEndInstancesUtils.checkIfInstanceAlreadyExist(instance);
            if (instanceValuesAlreadyExist) {
                LOG.debug("Back-end values already exist.");
                return new ResponseEntity<>(
                        "{\"message\": \"Back-end instance with given values already exist.\"}",
                        getHeaders(), HttpStatus.BAD_REQUEST);
            }

            backEndInstancesUtils.addNewBackEnd(instance);
            LOG.debug("Added new back-end.");
            return new ResponseEntity<>(
                    "{\"message\": \"Back-end instance with name '"
                    + instance.get("name").getAsString() + "' was successfully added to the back-end instance list.\"}",
                    getHeaders(), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("Error while adding instances: " + e.getMessage());
            String response = "{\"message\": \"Internal Error: " + e.getMessage() + "\"}";
            return new ResponseEntity<>(response, getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setLocation(URI.create("/"));
        return httpHeaders;
    }

}