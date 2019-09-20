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
}
