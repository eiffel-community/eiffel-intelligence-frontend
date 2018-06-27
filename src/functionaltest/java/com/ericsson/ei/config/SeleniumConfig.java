package com.ericsson.ei.config;

import com.ericsson.ei.frontend.exception.OSNotSupportedException;
<<<<<<< HEAD

import java.util.concurrent.TimeUnit;
=======
import com.google.common.io.Files;

import java.io.File;
>>>>>>> 5d51a4ee9b4ebcf602603f7e8fdf6839e4d537af

import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
<<<<<<< HEAD
=======
import org.openqa.selenium.firefox.FirefoxProfile;
>>>>>>> 5d51a4ee9b4ebcf602603f7e8fdf6839e4d537af
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeleniumConfig {

    private static final Logger log = LoggerFactory.getLogger(SeleniumConfig.class);
<<<<<<< HEAD

    public static FirefoxDriver getFirefoxDriver() throws OSNotSupportedException {
        FirefoxDriver driver;
=======
    private static File tempDownloadDirectory = Files.createTempDir();

    public static FirefoxDriver getFirefoxDriver() throws OSNotSupportedException {
        FirefoxDriver driver;
        FirefoxProfile firefoxProfile = new FirefoxProfile();

        firefoxProfile.setPreference("browser.download.folderList",2);
        firefoxProfile.setPreference("browser.download.dir", tempDownloadDirectory.getPath());
        firefoxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/json");

>>>>>>> 5d51a4ee9b4ebcf602603f7e8fdf6839e4d537af
        if (SystemUtils.IS_OS_LINUX) {
            System.setProperty("webdriver.gecko.driver", "src/functionaltest/resources/geckodriver");
        } else if (SystemUtils.IS_OS_WINDOWS) {
            System.setProperty("webdriver.gecko.driver", "src/functionaltest/resources/geckodriver.exe");
        } else {
            log.error("OS currently not supported.");
            throw new OSNotSupportedException();
        }
<<<<<<< HEAD
        FirefoxOptions firefoxOptions = new FirefoxOptions();//.setHeadless(true);

        driver = new FirefoxDriver(firefoxOptions);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        return driver;
    }

=======
        FirefoxOptions firefoxOptions = new FirefoxOptions()
                .setHeadless(true)
                .setProfile(firefoxProfile);

        driver = new FirefoxDriver(firefoxOptions);
        return driver;
    }

    public static File getTempDownloadDirectory() {
        return tempDownloadDirectory;
    }

>>>>>>> 5d51a4ee9b4ebcf602603f7e8fdf6839e4d537af
    public static String getBaseUrl(int randomServerPort) {
        String baseUrl = "http://localhost:" + randomServerPort;
        return baseUrl;
    }
}
