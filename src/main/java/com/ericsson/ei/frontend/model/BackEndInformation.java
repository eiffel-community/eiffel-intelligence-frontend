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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Component
@NoArgsConstructor
@AllArgsConstructor
public class BackEndInformation {
    private String name;

    @Value("${ei.backendServerHost:#{null}}")
    private String host;

    @Value("${ei.backendServerPort:#{null}}")
    private String port;

    @Value("${ei.backendContextPath:#{null}}")
    private String path;

    @Value("${ei.useSecureHttpBackend:#{false}}")
    @JsonProperty("https")
    @SerializedName("https")
    private boolean useSecureHttpBackend;

    private boolean active;
}