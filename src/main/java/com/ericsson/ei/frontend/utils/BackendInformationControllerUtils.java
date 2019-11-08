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

@Component
public class BackendInformationControllerUtils {

    private static final Logger LOG = LoggerFactory.getLogger(BackendInformationControllerUtils.class);

    @Autowired
    private BackendInstancesHandler backendInstancesUtils;

    /**
     * Processes a request to get all available back ends and returns
     * data containing all back end instances with added information
     * weather a back end is set as active or not.
     *
     * @param request
     * @return new ResponseEntity
     */
    public ResponseEntity<String> getBackendInstancesResponse(HttpServletRequest request) {
        try {
            JsonArray allAvailableInstances = backendInstancesUtils.getBackendInstancesAsJsonArray();

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

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setLocation(URI.create("/"));
        return httpHeaders;
    }

}