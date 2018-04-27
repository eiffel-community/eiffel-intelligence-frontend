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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Component
@NoArgsConstructor
@AllArgsConstructor
public class BackEndInformation {
    @JsonProperty("name")
    @NotBlank
    private String name;

    @JsonProperty("host")
    @Value("${ei.backendServerHost}")
    @NotBlank
    private String host;

    @JsonProperty("port")
    @Value("${ei.backendServerPort}")
    @NotNull @Min(1) @Max(65535)
    private int port;

    @JsonProperty("path")
    @Value("${ei.backendContextPath}")
    private String path;

    @JsonProperty("https")
    @Value("${ei.useSecureHttp}")
    private boolean https;

    @JsonIgnore
    private boolean checked;
}