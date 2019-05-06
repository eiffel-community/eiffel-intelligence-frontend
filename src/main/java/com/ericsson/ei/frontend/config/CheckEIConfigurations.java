package com.ericsson.ei.frontend.config;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


@Component
public class CheckEIConfigurations {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckEIConfigurations.class);
    
    @Autowired
    Environment env;
    
    @PostConstruct
    public void logAndCheckConfiguration() {
            
        LOGGER.debug("EI Frontend started with following configurations:\n"
                + "server.port: " + env.getProperty("server.port") + "\n"
                + "ei.frontend.service.port: " + env.getProperty("ei.frontend.service.port") + "\n"
                + "ei.frontend.service.host: " + env.getProperty("ei.frontend.service.host") + "\n"
                + "ei.frontend.context.path: " + env.getProperty("ei.frontend.context.path") + "\n"
                + "ei.use.secure.http.frontend: " + env.getProperty("ei.use.secure.http.frontend") + "\n"
                + "ei.backend.instances.filepath: " + env.getProperty("ei.backend.instances.filepath") + "\n"
                + "ei.backend.instances.list.json.content: " + env.getProperty("ei.backend.instances.list.json.content") + "\n"
                + "ei.eiffel.documentation.urls: " + env.getProperty("ei.eiffel.documentation.urls") + "\n"
                + "logging.level.root: " + env.getProperty("logging.level.root") + "\n"
                + "logging.level.org.springframework.web: " + env.getProperty("logging.level.org.springframework.web") + "\n"
                + "logging.level.com.ericsson.ei: " + env.getProperty("logging.level.com.ericsson.ei") + "\n"
                + "\nThese properties is only some of the configurations, more configurations may have been provided.\n");
    }
}
