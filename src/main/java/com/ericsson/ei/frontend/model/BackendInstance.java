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
package com.ericsson.ei.frontend.model;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Component
@NoArgsConstructor
@AllArgsConstructor
public class BackendInstance {

    private static final String NAME_KEY = "name";
    private static final String HOST_KEY = "host";
    private static final String PORT_KEY = "port";
    private static final String CONTEXT_PATH_KEY = "contextPath";
    private static final String HTTPS_KEY = "https";
    private static final String DEFAULT_KEY = "defaultBackend";

    private String name;
    private String host;
    private String port;
    private String contextPath;

    @JsonProperty("https")
    @SerializedName("https")
    private boolean useSecureHttpBackend;

    private boolean defaultBackend;

    /**
     * Get the class data as a JsonObject.
     *
     * @return
     *      JsonObject
     */
    public JsonObject getAsJsonObject() {
        JsonObject instance = new JsonObject();
        instance.addProperty(NAME_KEY, name);
        instance.addProperty(HOST_KEY, host);
        instance.addProperty(PORT_KEY, Integer.valueOf(port));
        instance.addProperty(CONTEXT_PATH_KEY, contextPath);
        instance.addProperty(HTTPS_KEY, useSecureHttpBackend);
        instance.addProperty(DEFAULT_KEY, defaultBackend);
        return instance;
    }

    /**
     * Get the Class url data as String.
     *
     * @return
     *      String
     */
    public String getUrlAsString() {
        String constructedUrl = "http";
        if (this.isUseSecureHttpBackend()) {
            constructedUrl = "https";
        }

        constructedUrl += "://" + host + ":" + port;

        if (this.getContextPath() != null && !this.getContextPath().isEmpty()) {
            constructedUrl += ("/" + contextPath).replaceAll("//", "/");
        }

        return constructedUrl;
    }
}