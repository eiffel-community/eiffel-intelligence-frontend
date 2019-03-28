package com.ericsson.ei.frontend.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import lombok.Setter;

@Setter
@Component
public class BackEndInstanceFileUtils {

    private static final Logger LOG = LoggerFactory.getLogger(BackEndInstanceFileUtils.class);

    private static final String BACKEND_INSTANCES_DEFAULT_FILENAME = "EIBackendInstancesInformation.json";
    private static final String EI_HOME_DEFAULT_NAME = ".eiffel-intelligence-frontend";

    private String eiInstancesPath;

    @Autowired
    BackEndInstancesUtils backendInstancesUtils;

    @Value("${ei.backend.instances.filepath:#{null}}")
    private String backendInstancesFilePath;

    @Value("${ei.backend.instances.list.json.content:#{null}}")
    private String backendInstancesListJsonContent;

    @PostConstruct
    public void init() throws IOException {
        LOG.info("Initiating BackEndInstanceFileUtils.");

        // Use home folder if a specific backendInstancesFilePath isn't provided
        boolean useUserHomeDirectory = (backendInstancesFilePath == null || backendInstancesFilePath.isEmpty());
        if (useUserHomeDirectory) {

            String homeFolder = System.getProperty("user.home");
            String eiHome = Paths.get(homeFolder, EI_HOME_DEFAULT_NAME).toString();

            Boolean eiHomeExists = Files.isDirectory(Paths.get(eiHome));
            if (!eiHomeExists) {
                createEiHomeFolder(eiHome);
            }

            Path eiInstancesListFilePath = Paths.get(eiHome, BACKEND_INSTANCES_DEFAULT_FILENAME);
            setEiInstancesPath(eiInstancesListFilePath.toString());
            File eiInstancesListFile = new File(eiInstancesListFilePath.toString());

            if (eiInstancesListFile.exists() && eiInstancesListFile.length() != 0) {
                LOG.debug("EI Instances List file path is not provided, but found a file in path: "  + eiInstancesPath +
                        "\nWill use that EI Instances List file.");
            }
            else {
                if (eiInstancesListFile.exists() && eiInstancesListFile.length() == 0) {
                    LOG.debug("EI Instances List file path is not provided, but found a file in path: "  + eiInstancesPath +
                            "\nThat EI Instances List file is empty!. Will try to create a default EI Instances List in that file.");
                }
                else {
                    LOG.debug("EI Instances List file path is not provided! " +
                            "Will create a default EI Instances List file at file path: " + eiInstancesPath);
                }
                LOG.info("Loading EI-Backend instances from EI Instances List file: " + eiInstancesPath);
                parseAndSetEiInstancesList();
            }

        } else {
            setEiInstancesPath(Paths.get(backendInstancesFilePath).toString());
            if (!(new File(eiInstancesPath).isFile())) {
                LOG.debug("EI Instances List file don' exist! Creating file with given or "
                        + "default EI instances list content. File path: " + eiInstancesPath);
                createFileWithDirs();
                parseAndSetEiInstancesList();
            } else {
                LOG.debug("EI-Backend instances list file that will be used: " + eiInstancesPath);
            }
        }
    }

    /**
     * Parses Ei Instances list from backendInstancesListJsonContent property and
     * sets default EI-Backend instance.
     *
     */
    private void parseAndSetEiInstancesList() {
        ensureValidFile();
        JsonArray parsedBackendInstancesListJsonArray = null;
        parsedBackendInstancesListJsonArray = parseEiInstancesListJsonObject();
        dumpJsonArray(parsedBackendInstancesListJsonArray);

        setDefaultEiBackendInstance(parsedBackendInstancesListJsonArray);
    }

    /**
     * Parses Ei Instances list from backendInstancesListJsonContent property.
     *
     * @return JsonArray
     */
    private JsonArray parseEiInstancesListJsonObject() {
        JsonArray backendInstancesListJsonArray = null;

        if (backendInstancesListJsonContent == null || backendInstancesListJsonContent.isEmpty()) {
            LOG.warn("EI backend instances list json object is empty."
                    + "\nMake sure that EI Instances list flags is set, "
                    + " 'ei.backend.instances.filepath' or 'ei.backend.instances.list.json.content'");
            return new JsonArray();
        }

        try {
            backendInstancesListJsonArray = (JsonArray) new JsonParser()
                    .parse(backendInstancesListJsonContent.toString());
        } catch (JsonSyntaxException e) {
            LOG.error("Failed to parse EI backend instances list json object."
                    + "\nMake sure ei.backend.instances.list.json.content property is set with one or more EI Backend instances."
                    + "\nError message: " + e.getMessage() + "\nErrors: " + e);
            System.exit(1);
        }
        return backendInstancesListJsonArray;
    }

    /**
     * Sets default EI Backend instance.
     *
     */
    private void setDefaultEiBackendInstance(JsonArray jArray) {
        for (JsonElement instanceJsonObj : jArray) {
            JsonObject jObject = instanceJsonObj.getAsJsonObject();
            if (Boolean.getBoolean(jObject.get("defaultBackend").toString())) {
                backendInstancesUtils.setDefaultBackEndInstance(jObject.get(BackEndInstancesUtils.NAME).toString(),
                        jObject.get(BackEndInstancesUtils.HOST).toString(),
                        Integer.parseInt(jObject.get(BackEndInstancesUtils.PORT).toString()),
                        jObject.get(BackEndInstancesUtils.CONTEXT_PATH).toString(),
                        Boolean.getBoolean(jObject.get(BackEndInstancesUtils.DEFAULT).toString()));
            }
        }
    }

    /**
     * Gets the JSON-data from file and returns as a JsonArray
     *
     * @return JsonArray
     */
    public JsonArray getInstancesFromFile() {
        LOG.debug("Reading EI Instances File: " + eiInstancesPath);
        try {
            ensureValidFile();

            JsonArray inputBackEndInstances = new JsonParser()
                    .parse(new String(Files.readAllBytes(Paths.get(eiInstancesPath)))).getAsJsonArray();
            if (inputBackEndInstances.isJsonNull() || inputBackEndInstances.size() < 1) {
                LOG.debug("EI Instance file does not exist or is empty. Returnin an empty EI Instance json list.");
                return new JsonArray();
            }
            return inputBackEndInstances;
        } catch (Exception e) {
            LOG.error("Failure when try to parse EI instances json file. Error: " + e.getMessage());
            return new JsonArray();
        }
    }

    /**
     * Saves the given JsonArray
     *
     * @param jsonArrayToDump JsonArray
     */
    public void dumpJsonArray(JsonArray jsonArrayToDump) {
        try {
            Files.write(Paths.get(eiInstancesPath), jsonArrayToDump.toString().getBytes());
        } catch (IOException e) {
            LOG.error("Couldn't add EI instance to EI Instances file. Error: " + e.getMessage());
        }

    }

    private void ensureValidFile() {
        try {
            if (!(new File(eiInstancesPath).isFile())) {
                createFileWithDirs();
                return;
            }

            if (!fileContainsJsonArray()) {
                LOG.error("EI Instances file does not contain valid json! JSON:' "
                        + new String(Files.readAllBytes(Paths.get(eiInstancesPath))) + "'.");
                System.exit(-1);
            }
        } catch (Exception e) {
            String message = String.format(
                    "Failed to read backendInstancesFilePath %s. Please check access rights or choose another backendInstancesFilePath in application.properties.",
                    eiInstancesPath);
            LOG.error(message);
            System.exit(-1);
        }
    }

    private void createFileWithDirs() throws IOException {
        File eiInstancesParentFolder = Paths.get(eiInstancesPath).getParent().toFile();

        if (!(eiInstancesParentFolder.isDirectory())) {
            LOG.debug(String.format("Parentdir(s) for %s does not exist! Trying to create necessary parent dirs.",
                    backendInstancesFilePath));
            eiInstancesParentFolder.mkdirs();
        }

        LOG.debug("EI Instances file does not exist! Trying to create file.");
        Files.createFile(Paths.get(eiInstancesPath));
        PrintWriter eiInstanceFilePrintWriter = new PrintWriter(eiInstancesPath);
        eiInstanceFilePrintWriter.println("[]");
        eiInstanceFilePrintWriter.close();
    }

    private boolean fileContainsJsonArray() {
        try {
            new JsonParser().parse(new String(Files.readAllBytes(Paths.get(eiInstancesPath)))).getAsJsonArray();
            return true;
        } catch (Exception e) {
            LOG.error("Failure when try to parse EI Instances json file. Error: " + e.getMessage());
            return false;
        }
    }

    private void createEiHomeFolder(String eiHome) throws IOException {
        Boolean success = (new File(eiHome)).mkdirs();

        if (!success) {
            String message = String.format(
                    "Failed to create eiffel intelligence home folder in %s. Please check access rights or choose a specific backendInstancesFilePath in application.properties.",
                    eiHome);
            LOG.error(message);
            System.exit(-1);
        }
    }

}
