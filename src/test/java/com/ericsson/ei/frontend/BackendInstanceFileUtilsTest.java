/*
   Copyright 2017 Ericsson AB.
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
package com.ericsson.ei.frontend;

import static org.junit.Assert.assertEquals;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ericsson.ei.frontend.utils.BackEndInstanceFileUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class BackendInstanceFileUtilsTest {

    private static final String BACKEND_INSTANCES_FILE_PATH = "src/test/resources/backendInstances/backendInstances.json";
    private static final String FILE_TO_WRITE = "src/test/resources/backendInstances/fileToWriteInstances.json";

    @Autowired
    private BackEndInstanceFileUtils fileUtils;

    private JsonArray instances;

    @Before
    public void before() throws IOException {
        instances = new JsonParser().parse(new FileReader(BACKEND_INSTANCES_FILE_PATH)).getAsJsonArray();

    }

    @Test
    public void testWriteToAndReadFromFile() throws IOException {
        fileUtils.setEiInstancesPath(FILE_TO_WRITE);
        fileUtils.dumpJsonArray(instances);

        JsonArray infoFromFile = new JsonParser().parse(new String(Files.readAllBytes(Paths.get(FILE_TO_WRITE))))
                .getAsJsonArray();
        assertEquals(infoFromFile, fileUtils.getInstancesFromFile());
        Files.deleteIfExists(Paths.get(FILE_TO_WRITE));
    }
}
