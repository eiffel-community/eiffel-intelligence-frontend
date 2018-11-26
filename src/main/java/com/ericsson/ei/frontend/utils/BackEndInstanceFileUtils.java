package com.ericsson.ei.frontend.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ericsson.ei.frontend.model.BackEndInformation;
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
    
    @Value("${ei.backendInstancesFilePath:#{null}}")
    private String backendInstancesFilePath;
    
    @Value("${ei.backendInstancesListJsonContent:#{null}}")
    private String backendInstancesListJsonContent;

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
            LOG.info("EI Instances List file path is not provided! " +
                     "Will create a default EI Instances List file at file path: " + eiInstancesPath);
            
            parseAndSetEiInstancesList();
            
        } else {
            setEiInstancesPath(Paths.get(backendInstancesFilePath).toString());
            if (!(new File(eiInstancesPath).isFile())) {
            	LOG.info("EI Instances List file don' exist! Creating file with given or " +
                         "default EI instances list content. File path: " + eiInstancesPath);
                createFileWithDirs();
                parseAndSetEiInstancesList();
            }
            else {
            	LOG.info("EI-Backend instances list file that will be used: " + eiInstancesPath);
            }
            
        }
    }


    /**
     * Parses Ei Instances list from backendInstancesListJsonContent property
     * and sets default EI-Backend instance.
     * 
     */
    private void parseAndSetEiInstancesList() {
        JsonArray parsedBackendInstancesListJsonArray = null;
        parsedBackendInstancesListJsonArray = parseEiInstancesListJsonObject();
        dumpJsonArray(parsedBackendInstancesListJsonArray);
        try {
			ensureValidFile();
		} catch (IOException e) {
			LOG.error("Failed to validate EI Instances List json object." +
		               "\nError message: " + e.getMessage() +
		               "\nErrors: " + e);
		}
        setDefaultEiBackendInstance(parsedBackendInstancesListJsonArray);
    }


    /**
     * Parses Ei Instances list from backendInstancesListJsonContent property.
     * 
     * @return
     *      JsonArray
     */
    private JsonArray parseEiInstancesListJsonObject() {
        JsonArray backendInstancesListJsonArray = null;
        
        if ( backendInstancesListJsonContent == null || backendInstancesListJsonContent.isEmpty()) {
        	LOG.error("EI backned instances list json object is empty, can't continue." +
                      "\nMake sure that EI Instances list flags is set, " +
        			  " 'ei.backendInstancesFilePath' or 'ei.backendInstancesListJsonContent'");
      	System.exit(1);
        }
        
        try {
        	backendInstancesListJsonArray = (JsonArray) new JsonParser().parse(backendInstancesListJsonContent.toString());
        }
        catch (JsonSyntaxException e) {
        	LOG.error("Failed to parse EI backned instances list json object." +
                      "\nMake sure ei.backendInstancesListJsonContent properties is set with one or more EI Backend instances." +
        			  "\nError message: " + e.getMessage() +
        			  "\nErrors: " + e);
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
