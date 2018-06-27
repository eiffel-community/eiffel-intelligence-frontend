package com.ericsson.ei.config;

import com.ericsson.ei.frontend.exception.OSNotSupportedException;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeleniumConfig {

    private static final Logger log = LoggerFactory.getLogger(SeleniumConfig.class);

    public static FirefoxDriver getFirefoxDriver() throws OSNotSupportedException {
        FirefoxDriver driver;
        if (SystemUtils.IS_OS_LINUX) {
            System.setProperty("webdriver.gecko.driver", "src/functionaltest/resources/geckodriver");
        } else if (SystemUtils.IS_OS_WINDOWS) {
            System.setProperty("webdriver.gecko.driver", "src/functionaltest/resources/geckodriver.exe");
        } else {
            log.error("OS currently not supported.");
            throw new OSNotSupportedException();
        }
        FirefoxOptions firefoxOptions = new FirefoxOptions();//.setHeadless(true);

        driver = new FirefoxDriver(firefoxOptions);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        return driver;
    }

    public static String getBaseUrl(int randomServerPort) {
        String baseUrl = "http://localhost:" + randomServerPort;
        return baseUrl;
    }
}
