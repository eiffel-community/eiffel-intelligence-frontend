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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ericsson.ei.frontend.exceptions.EiBackendInstancesException;
import com.ericsson.ei.frontend.model.BackendInstance;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class BackendInstancesHandler {
    private static final Logger LOG = LoggerFactory.getLogger(BackendInstancesHandler.class);

    @Value("${ei.backend.instances.list.json.content:#{null}}")
    private String backendInstancesJsonString;
    private List<BackendInstance> backendInstances = new ArrayList<>();

    @PostConstruct
    private void init() {
        JsonArray backendInstancesJsonArray = parseBackendInstancesJsonString();
        backendInstances = parseBackendInstancesJsonArray(backendInstancesJsonArray);
    }

    private JsonArray parseBackendInstancesJsonString() {
        JsonParser parser = new JsonParser();
        JsonElement backendInstancesJsonElement = parser.parse(backendInstancesJsonString);
        JsonArray backendInstancesJsonArray = backendInstancesJsonElement.getAsJsonArray();
        return backendInstancesJsonArray;
    }

    /**
     * Returns the backendInstances as JsonArray
     * @return JsonArray
     */
    public JsonArray getBackendInstancesAsJsonArray() {
        JsonArray backendJsonArray = new JsonArray();
        for (BackendInstance backendInstance : backendInstances) {
            backendJsonArray.add(backendInstance.getAsJsonObject());
        }
        return backendJsonArray;
    }

    /**
     * Returns the wanted BackendInstance
     *
     * @param backendName
     * @return BackendInstance
     * @throws EiBackendInstancesException
     */
    public BackendInstance getBackendInstance(String backendName) throws EiBackendInstancesException {
        for(BackendInstance backendInstance: backendInstances) {
            if(backendInstance.getName().equals(backendName)) {
                LOG.debug("Returning backend " + backendName + ".");
                return backendInstance;
            }
        }

        throw new EiBackendInstancesException(
                "EI Backend instance '" + backendName +  "' does not exist in EI Backend Instances list.");
        }

    /**
     * Returns the default backend if set, otherwise it will return
     * the first instance in the list.
     *
     * @return BackendInstance
     */
    public BackendInstance getDefaultBackendInstance() {
        for(BackendInstance backendInstance: backendInstances) {
            if(backendInstance.isDefaultBackend()) {
                return backendInstance;
            }
        }

        return backendInstances.get(0);
    }

    /**
     * Sets the backendInstances list, useful when mocking for tests
     *
     * @param backendInstances
     */
    public void setBackendInstances(JsonArray backendInstancesJsonArray) {
        cleanBackendInstances();
        backendInstances = parseBackendInstancesJsonArray(backendInstancesJsonArray);
    }

    private List<BackendInstance> parseBackendInstancesJsonArray(JsonArray backendInstancesJsonArray) {
        try {
            if(backendInstancesJsonString == null && backendInstancesJsonString.isEmpty()) {
                LOG.debug("No backend instances provided.");
                return null;
            }

            List<BackendInstance> backendInstances = new ArrayList<>();
            for(Object jsonElement: backendInstancesJsonArray) {
                JsonObject backendInstanceJsonObject = (JsonObject)jsonElement;
                BackendInstance backendInstance = new BackendInstance();
                backendInstance.setName(backendInstanceJsonObject.get("name").getAsString());
                backendInstance.setHost(backendInstanceJsonObject.get("host").getAsString());
                backendInstance.setPort(backendInstanceJsonObject.get("port").getAsString());
                backendInstance.setContextPath(backendInstanceJsonObject.get("contextPath").getAsString());
                backendInstance.setUseSecureHttpBackend(backendInstanceJsonObject.get("https").getAsBoolean());
                backendInstance.setDefaultBackend(backendInstanceJsonObject.get("defaultBackend").getAsBoolean());

                backendInstances.add(backendInstance);
            }
            return backendInstances;
        } catch (JSONException e) {
            LOG.error("Incorrect instances provided. Please check application properties so that name, host, port, "
                    + "contextPath, https and defaultBackend fields exists. Stacktrace {}", ExceptionUtils.getStackTrace(e));
            System.exit(-1);
            return null;
        }
    }

    private void cleanBackendInstances() {
        backendInstances = new ArrayList<>();
    }
}