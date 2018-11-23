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

import org.springframework.beans.factory.annotation.Value;
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
public class BackEndInformation {
    private static final String NAME = "name";
    private static final String HOST = "host";
    private static final String PORT = "port";
    private static final String PATH = "path";
    private static final String HTTPS = "https";
    private static final String DEFAULT = "defaultBackend";

    private String name;
    private String host;
    private String port;
    private String path;

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
        instance.addProperty(NAME, name);
        instance.addProperty(HOST, host);
        instance.addProperty(PORT, Integer.valueOf(port));
        instance.addProperty(PATH, path);
        instance.addProperty(HTTPS, useSecureHttpBackend);
        instance.addProperty(DEFAULT, defaultBackend);
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

        if (this.getPath() != null && !this.getPath().isEmpty()) {
            constructedUrl += ("/" + path).replaceAll("//", "/");
        }

        return constructedUrl;
    }
}