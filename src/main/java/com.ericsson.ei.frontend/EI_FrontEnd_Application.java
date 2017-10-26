package com.ericsson.ei.frontend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EI_FrontEnd_Application {

    public static final Logger log = LoggerFactory.getLogger(EI_FrontEnd_Application.class);

    public static void main(String[] args) {
        SpringApplication.run(EI_FrontEnd_Application.class, args);
    }
}
