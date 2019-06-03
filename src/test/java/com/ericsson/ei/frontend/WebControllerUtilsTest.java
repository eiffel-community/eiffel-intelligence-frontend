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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ericsson.ei.frontend.utils.WebControllerUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WebControllerUtilsTest {
	private static final String HOST = "testHost";
	private static final int PORT = 12345;
	private static final String CONTEXT_PATH = "/path";
    private WebControllerUtils controllerUtils;

    @Test
    public void testGetFrontEndServiceUrl() throws Exception {
      String expectedUrl = String.format("http://%s:%s%s", HOST, PORT, CONTEXT_PATH);
      controllerUtils = new WebControllerUtils(HOST, PORT, CONTEXT_PATH, false, null, null, null, null, null);
      assertEquals(expectedUrl, controllerUtils.getFrontEndServiceUrl());
    }

    @Test
    public void test() throws Exception {
        String regExForInvalidName = null;

        try {
            regExForInvalidName = getValue("invalidName");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String regExForValidEmail = null;
        try {
            regExForValidEmail = getValue("validEmail");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        assertEquals(regExForInvalidName, "(\\W)");
        assertEquals(regExForValidEmail,
                "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");
    }

    /**
     * This function takes a key and returns the corresponding value from the
     * JSONObject
     *
     * @param String
     *            key
     * @return String value
     */
    public String getValue(String key) {
        String value = null;
        value = controllerUtils.getRegEx(key);
        return value;
    }

}
