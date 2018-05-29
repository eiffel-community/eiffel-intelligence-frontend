package com.ericsson.ei.config;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class SeleniumConfig {

    public static FirefoxDriver getFirefoxDriver() {
        FirefoxDriver driver;
        System.setProperty("webdriver.gecko.driver", "/usr/bin/geckodriver");
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
