package com.ericsson.ei.frontend.model;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
@ToString
@Component
@NoArgsConstructor
@AllArgsConstructor
public class ListWrapper {
    @Valid
    private List<BackEndInformation> backEndInformation;
}