package com.ericsson.ei.frontend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    @NotNull
    private int port;

    @JsonProperty("path")
    @Value("${ei.backendContextPath}")
    private String path;

    @JsonProperty("https")
    @Value("${ei.useSecureHttp}")
    private boolean https;
}