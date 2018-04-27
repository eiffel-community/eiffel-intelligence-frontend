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
package com.ericsson.ei.frontend.utils;

import com.ericsson.ei.frontend.model.BackEndInformation;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
public class BackEndInstancesUtils {

    private static final Logger LOG = LoggerFactory.getLogger(BackEndInstancesUtils.class);

    @Value("${ei.backendServerHost}")
    private String host;

    @Value("${ei.backendServerPort}")
    private int port;

    @Value("${ei.backendContextPath}")
    private String path;

    @Value("${ei.useSecureHttp}")
    private boolean https;

    @Value("${ei.backendInstancesPath}")
    private String eiInstancesPath;

    @Autowired
    private BackEndInformation backEndInformation;

    private List<BackEndInformation> information = new ArrayList<>();
    private JSONArray instances = new JSONArray();

    @PostConstruct
    public void init() {
        parseBackEndInstancesFile();
        if (!checkIfInstanceAlreadyExist(getCurrentInstance())) {
            instances.put(getCurrentInstance());
        }
        writeIntoFile();
    }

    private JSONObject getCurrentInstance() {
        JSONObject instance = new JSONObject();
        instance.put("name", "core");
        instance.put("host", host);
        instance.put("port", port);
        instance.put("path", path);
        instance.put("https", https);
        instance.put("checked", true);
        return instance;
    }

    public void setBackEndProperties(BackEndInformation properties) {
        backEndInformation.setName(properties.getName());
        backEndInformation.setHost(properties.getHost());
        backEndInformation.setPort(properties.getPort());
        backEndInformation.setPath(properties.getPath());
        backEndInformation.setHttps(properties.isHttps());
    }

    public boolean checkIfInstanceAlreadyExist(JSONObject instance) {
        for (int i = 0; i < instances.length(); i++) {
            if (instances.getJSONObject(i).get("host").equals(instance.get("host")) &&
                    instances.getJSONObject(i).get("port").equals(instance.get("port")) &&
                    instances.getJSONObject(i).get("path").equals(instance.get("path"))) {
                return true;
            }
        }
        return false;
    }

    public void writeIntoFile() {
        if (eiInstancesPath != null) {
            try {
                FileWriter fileWriter = new FileWriter(eiInstancesPath);
                fileWriter.append(instances.toString());
                fileWriter.flush();
            } catch (IOException e) {
                LOG.error("Couldn't add instance to file " + e.getMessage());
            }
        }
    }

    public void parseBackEndInstancesFile() {
        if (eiInstancesPath != null) {
            try {
                JSONArray inputBackEndInstances = new JSONArray(new String(Files.readAllBytes(Paths.get(eiInstancesPath))));
                for (Object o : inputBackEndInstances) {
                    JSONObject instance = (JSONObject) o;
                    if (!checkIfInstanceAlreadyExist(instance)) {
                        information.add(new ObjectMapper().readValue(instance.toString(), BackEndInformation.class));
                        instances.put(instance);
                    }
                }
            } catch (IOException e) {
                LOG.error("Failure when try to parse json file" + e.getMessage());
            }
        }
    }
}