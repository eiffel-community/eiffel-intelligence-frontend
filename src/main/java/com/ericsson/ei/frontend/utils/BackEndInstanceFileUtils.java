package com.ericsson.ei.frontend.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import lombok.Setter;

@Setter
@Component
public class BackEndInstanceFileUtils {

    private static final Logger LOG = LoggerFactory.getLogger(BackEndInstanceFileUtils.class);

    private static final String BACKEND_INSTANCES_FILENAME = "EIBackendInstancesInformation.json";
    private static final String EI_HOME_DEFAULT_NAME = ".eiffel";

    @Value("${ei.backendInstancesPath:#{null}}")
    private String eiInstancesPath;

    @Value("${ei.home}")
    private String eiHome;

    @PostConstruct
    public void init() throws IOException {
        LOG.info("Initiating BackEndInstanceFileUtils.");

        // If user did not choose ei home folder, set EI home folder to default
        if(eiHome == null || eiHome.isEmpty()) {
            String homeFolder = System.getProperty("user.home");
            eiHome = Paths.get(homeFolder, EI_HOME_DEFAULT_NAME).toString();
        }

        Boolean eiHomeExists = Files.isDirectory(Paths.get(eiHome));
        if (!eiHomeExists) {
            createEiHomeFolder();
        }

        // If user did not choose eiInstancesPath, use EI home folder.
        if(eiInstancesPath == null || eiInstancesPath.isEmpty()) {
            setEiInstancesPath(Paths.get(eiHome, BACKEND_INSTANCES_FILENAME).toString());
        }
    }

    /**
     *Gets the JSON-data from file and returns as a JsonArray
     *
     * @return
     *      JsonArray
     */
    public JsonArray getInstancesFromFile() {
        try {
            ensureValidFile();

            JsonArray inputBackEndInstances = new JsonParser().parse(new String(Files.readAllBytes(Paths.get(eiInstancesPath)))).getAsJsonArray();
            return inputBackEndInstances;
        } catch (IOException e) {
            LOG.error("Failure when try to parse json file" + e.getMessage());
            return new JsonArray();
        }
    }

    /**
     * Saves the given JsonArray
     *
     * @param jsonArrayToDump
     *      JsonArray
     */
    public void dumpJsonArray(JsonArray jsonArrayToDump) {
        try {
            ensureValidFile();
            Files.write(Paths.get(eiInstancesPath), jsonArrayToDump.toString().getBytes());
        } catch (IOException e) {
            LOG.error("Couldn't add instance to file " + e.getMessage());
        }

    }

    private void ensureValidFile() throws IOException {
        if (!(new File(eiInstancesPath).isFile())) {
            LOG.error("File does not exist! Trying to creat file.");
            Files.createFile(Paths.get(eiInstancesPath));
            Files.write(Paths.get(eiInstancesPath), "[]".getBytes());
            return;
        }

        if (!fileContainsJsonArray()) {
            LOG.error("File does not contain valid json! JSON:'" + new String(Files.readAllBytes(Paths.get(eiInstancesPath))) + "'.");
        }
    }

    private boolean fileContainsJsonArray() {
        try {
            new JsonParser().parse(new String(Files.readAllBytes(Paths.get(eiInstancesPath)))).getAsJsonArray();
            return true;
        } catch (Exception e) {
            LOG.error("Failure when try to parse json file" + e.getMessage());
            return false;
        }
    }

    private void createEiHomeFolder() {
        Boolean success = (new File(eiHome)).mkdirs();

        if (!success) {
           LOG.error("Failed to create eiffel intelligence home folder in {}. Please check access rights or change default folder in application.properties.".format(eiHome));
           System.exit(-1);
        }
    }

}
