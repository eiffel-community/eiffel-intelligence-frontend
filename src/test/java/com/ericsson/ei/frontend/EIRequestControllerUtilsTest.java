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
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ericsson.ei.frontend.model.BackEndInformation;
import com.ericsson.ei.frontend.utils.BackEndInstancesUtils;
import com.ericsson.ei.frontend.utils.EIRequestsControllerUtils;
import com.ericsson.ei.frontend.utils.WebControllerUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EIRequestControllerUtilsTest {

    @Autowired
    EIRequestsControllerUtils eiRequestsControllerUtils;

    @MockBean
    private WebControllerUtils controllerUtils;

    @MockBean
    private BackEndInstancesUtils backEndInstancesUtils;

    private HttpServletRequest  mockedRequest;

    //private BackEndInformation backendInformation;

    @Before
    public void beforeClass() {
        BackEndInformation backendInformation = new BackEndInformation("TestName", "TestHost", "12345", "", false, true);
        BackEndInformation backendInformationNull = new BackEndInformation("NullName", "NullHost", "12345", "", false, false);
        mockedRequest = Mockito.mock(HttpServletRequest.class);

        when(backEndInstancesUtils.getBackEndInformationByName("TestName")).thenReturn(backendInformation);
        when(backEndInstancesUtils.getBackEndInformationByName(null)).thenReturn(backendInformationNull);

        when(mockedRequest.getMethod()).thenReturn("Test");
    }

    @Test
    public void testGetEIRequestURL() throws Exception {
        String url = null;

        // Test name null and no querystring.
        when(mockedRequest.getServletPath()).thenReturn("/subscription");
        when(mockedRequest.getQueryString()).thenReturn(null);
        url = eiRequestsControllerUtils.getEIRequestURL(mockedRequest);
        assertEquals("http://NullHost:12345/subscription", url);

        // Test name TestName and querystring.
        when(mockedRequest.getServletPath()).thenReturn("/query");
        when(mockedRequest.getQueryString()).thenReturn("someQuary=Something");
        url = eiRequestsControllerUtils.getEIRequestURL(mockedRequest);
        assertEquals("http://NullHost:12345/query?someQuary=Something", url);

        // Test url given in input from request.
        when(mockedRequest.getServletPath()).thenReturn("/subscription");
        when(mockedRequest.getQueryString()).thenReturn("backendurl=https://inPutBackEndUrl:98765");
        url = eiRequestsControllerUtils.getEIRequestURL(mockedRequest);
        assertEquals("https://inPutBackEndUrl:98765/subscription", url);

        // Test name given in input from request.
        when(mockedRequest.getServletPath()).thenReturn("/subscription");
        when(mockedRequest.getQueryString()).thenReturn("backendname=TestName");
        url = eiRequestsControllerUtils.getEIRequestURL(mockedRequest);
        assertEquals("http://TestHost:12345/subscription", url);
    }
}
