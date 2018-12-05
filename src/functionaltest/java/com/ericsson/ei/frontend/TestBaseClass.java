package com.ericsson.ei.frontend;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ericsson.ei.config.SeleniumConfig;
import com.ericsson.ei.frontend.utils.BackEndInstanceFileUtils;
import com.ericsson.ei.frontend.utils.BackEndInstancesUtils;
import com.ericsson.ei.frontend.utils.WebControllerUtils;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestBaseClass {
    @LocalServerPort
    protected int testServerPort;

    @Autowired
    WebControllerUtils webControllerUtils;

    private String filePath = "";

    @Autowired
    private BackEndInstanceFileUtils backEndInstanceFileUtils;

    @Autowired
    protected BackEndInstancesUtils backEndInstancesUtils;

    @PostConstruct
    public void init() throws Exception {
        File tempFile = File.createTempFile("tempfile", ".json");
        tempFile.deleteOnExit();

        filePath = tempFile.getAbsolutePath().toString();
        Files.write(Paths.get(filePath), "[]".getBytes());
        backEndInstanceFileUtils.setEiInstancesPath(filePath);

        backEndInstancesUtils.setDefaultBackEndInstanceToNull();
        backEndInstancesUtils.setDefaultBackEndInstance("test", "localhost", 12345, "", true);

        webControllerUtils.setFrontendServicePort(testServerPort);
    }
}
