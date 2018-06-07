package com.ericsson.ei.config;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class SeleniumConfig {

    public static FirefoxDriver getFirefoxDriver() {
        FirefoxDriver driver;
        if(SystemUtils.IS_OS_LINUX) {
            System.setProperty("webdriver.gecko.driver", "src/functionaltest/resources/geckodriver");
        } else if (SystemUtils.IS_OS_WINDOWS) {
            System.setProperty("webdriver.gecko.driver", "src/functionaltest/resources/geckodriver.exe");
        } else {
            System.exit(1);
        }
        FirefoxOptions firefoxOptions = new FirefoxOptions().setHeadless(true);

        driver = new FirefoxDriver(firefoxOptions);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        return driver;
    }

    public static String getBaseUrl(int randomServerPort) {
        String baseUrl = "http://localhost:" + Integer.toString(randomServerPort);
        return baseUrl;
    }
}
