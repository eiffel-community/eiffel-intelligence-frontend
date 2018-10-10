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

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ericsson.ei.frontend.model.BackEndInformation;
import com.google.gson.JsonObject;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class BackEndInformationTest {

    private static final String NAME = "name";
    private static final String HOST = "host";
    private static final String PORT = "port";
    private static final String PATH = "path";
    private static final String HTTPS = "https";
    private static final String DEFAULT = "defaultBackend";

    private static final String BACK_END_NAME = "Test_name";
    private static final String BACK_END_HOST = "Test_host";
    private static final String BACK_END_PORT = "12345";

    private BackEndInformation backEndInformation;

    private static final Logger LOG = LoggerFactory.getLogger(BackEndInformationTest.class);

    @Before
    public void before() throws IOException {
        backEndInformation = new BackEndInformation(BACK_END_NAME, BACK_END_HOST, BACK_END_PORT, "", true, false);
    }

    @Test
    public void testGetAsJsonObject() {
        JsonObject instance = new JsonObject();
        instance.addProperty(NAME, BACK_END_NAME);
        instance.addProperty(HOST, BACK_END_HOST);
        instance.addProperty(PORT, Integer.valueOf(BACK_END_PORT));
        instance.addProperty(PATH, "");
        instance.addProperty(HTTPS, true);
        instance.addProperty(DEFAULT, false);

        assertEquals(instance, backEndInformation.getAsJsonObject());
    }

    @Test
    public void testGetUrlAsString() {
        String urlWithHttp = String.format("http://%s:%s", BACK_END_HOST, BACK_END_PORT);
        String urlWithHttpwothPath = String.format("http://%s:%s%s", BACK_END_HOST, BACK_END_PORT, "/path/");
        String urlWithHttps = String.format("https://%s:%s", BACK_END_HOST, BACK_END_PORT);
        LOG.error("urlWithHttp = " + urlWithHttp);
        LOG.error("urlWithHttpwothPath = " + urlWithHttpwothPath);
        LOG.error("urlWithHttps = " + urlWithHttps);
        backEndInformation.setUseSecureHttpBackend(true);
        assertEquals(urlWithHttps, backEndInformation.getUrlAsString());

        backEndInformation.setUseSecureHttpBackend(false);
        assertEquals(urlWithHttp, backEndInformation.getUrlAsString());

        backEndInformation.setPath("/path/");
        LOG.error("backEndInformation = " + backEndInformation.getUrlAsString());
        assertEquals(urlWithHttpwothPath, backEndInformation.getUrlAsString());

    }
}
