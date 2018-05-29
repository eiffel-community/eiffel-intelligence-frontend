package com.ericsson.ei.frontend;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("ei.frontendServiceHost", "localhost");
        System.setProperty("ei.frontendServicePort", "9090");
        System.setProperty("ei.frontendContextPath", "somePath");
        System.setProperty("ei.useSecureHttp", "false");
    }

    @Test
    public void testIndex() throws Exception {
        testGet("/");
    }

    @Test
    public void testSubscription() throws Exception {
        testGet("/subscriptionpage.html");
    }

    @Test
    public void testRules() throws Exception {
        testGet("/testRules.html");
    }

    @Test
    public void testEiInfo() throws Exception {
        testGet("/eiInfo.html");
    }

    @Test
    public void testLogin() throws Exception {
        testGet("/login.html");
    }

    @Test
    public void testAddInstances() throws Exception {
        testGet("/add-instances.html");
    }

    @Test
    public void testSwitchBackend() throws Exception {
        testGet("/switch-backend.html");
    }

    private void testGet(String path) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(path)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(model().attribute("frontendServiceUrl", "http://localhost:9090/somePath"))
            .andDo(print())
            .andReturn();
    }

    @AfterClass
    public static void afterClass() {
        System.clearProperty("ei.frontendServiceHost");
        System.clearProperty("ei.frontendServicePort");
        System.clearProperty("ei.frontendContextPath");
        System.clearProperty("ei.useSecureHttp");
    }

}
