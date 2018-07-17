package com.ericsson.ei.config;

import com.ericsson.ei.frontend.exception.OSNotSupportedException;

import com.google.common.io.Files;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class SeleniumConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeleniumConfig.class);

    private static File tempDownloadDirectory = Files.createTempDir();

    public static FirefoxDriver getFirefoxDriver() throws OSNotSupportedException {
        FirefoxDriver driver;
        FirefoxProfile firefoxProfile = new FirefoxProfile();

        firefoxProfile.setPreference("browser.download.folderList", 2);
        firefoxProfile.setPreference("browser.download.dir", tempDownloadDirectory.getPath());
        firefoxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/json");

        if (SystemUtils.IS_OS_LINUX) {
            System.setProperty("webdriver.gecko.driver", "src/functionaltest/resources/geckodriver");
        } else if (SystemUtils.IS_OS_WINDOWS) {
            System.setProperty("webdriver.gecko.driver", "src/functionaltest/resources/geckodriver.exe");
        } else {
            LOGGER.error("OS currently not supported.");
            throw new OSNotSupportedException();
        }

        FirefoxOptions firefoxOptions = new FirefoxOptions()
                .setHeadless(true)
                .setProfile(firefoxProfile);

        driver = new FirefoxDriver(firefoxOptions);
        return driver;
    }

    public static File getTempDownloadDirectory() {
        return tempDownloadDirectory;
    }

    public static String getBaseUrl(int randomServerPort) {
        return "http://localhost:" + randomServerPort;
    }
}
