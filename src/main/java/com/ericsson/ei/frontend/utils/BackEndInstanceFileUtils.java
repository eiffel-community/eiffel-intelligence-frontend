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

    private static final String BACKEND_INSTANCES_DEFAULT_FILENAME = "EIBackendInstancesInformation.json";
    private static final String EI_HOME_DEFAULT_NAME = ".eiffel-intelligence-frontend";

    private String eiInstancesPath;

    @Value("${ei.backendInstancesFilePath:#{null}}")
    private String backendInstancesFilePath;

    @PostConstruct
    public void init() throws IOException {
        LOG.info("Initiating BackEndInstanceFileUtils.");

        // Use home folder if a specific backendInstancesFilePath isn't provided
        if(backendInstancesFilePath == null || backendInstancesFilePath.isEmpty()) {
            String homeFolder = System.getProperty("user.home");
            String eiHome = Paths.get(homeFolder, EI_HOME_DEFAULT_NAME).toString();

            Boolean eiHomeExists = Files.isDirectory(Paths.get(eiHome));
            if (!eiHomeExists) {
                createEiHomeFolder(eiHome);
            }

            setEiInstancesPath(Paths.get(eiHome, BACKEND_INSTANCES_DEFAULT_FILENAME).toString());
        } else {
            setEiInstancesPath(Paths.get(backendInstancesFilePath).toString());
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
        try {
            if (!(new File(eiInstancesPath).isFile())) {
                createFileWithDirs();
                return;
            }

            if (!fileContainsJsonArray()) {
                LOG.error("File does not contain valid json! JSON:'" + new String(Files.readAllBytes(Paths.get(eiInstancesPath))) + "'.");
                System.exit(-1);
            }
        } catch(Exception e) {
            String message = String.format(
                    "Failed to read backendInstancesFilePath %s. Please check access rights or choose another backendInstancesFilePath in application.properties.", eiInstancesPath);
            LOG.error(message);
            System.exit(-1);
        }
    }

    private void createFileWithDirs() throws IOException {
        File eiInstancesParentFolder = Paths.get(eiInstancesPath).getParent().toFile();

        if (!(eiInstancesParentFolder.isDirectory())){
            LOG.info(String.format("Parentdir(s) for %s does not exist! Trying to create necessary parent dirs.", backendInstancesFilePath));
            eiInstancesParentFolder.mkdirs();
        }

        LOG.info("File does not exist! Trying to create file.");
        Files.createFile(Paths.get(eiInstancesPath));
        Files.write(Paths.get(eiInstancesPath), "[]".getBytes());
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

    private void createEiHomeFolder(String eiHome) throws IOException {
        Boolean success = (new File(eiHome)).mkdirs();

        if (!success) {
            String message = String.format(
                    "Failed to create eiffel intelligence home folder in %s. Please check access rights or choose a specific backendInstancesFilePath in application.properties.", eiHome);
            LOG.error(message);
            System.exit(-1);
        }
    }

}
