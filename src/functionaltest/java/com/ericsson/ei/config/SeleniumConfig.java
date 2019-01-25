package com.ericsson.ei.config;

import com.ericsson.ei.frontend.exception.OSNotSupportedException;
import com.ericsson.ei.frontend.exception.PropertiesNotLoadedException;
import com.google.common.io.Files;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.firefox.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

import java.io.File;

public class SeleniumConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeleniumConfig.class);

    private static String propertiesPath = String.join(File.separator, "src", "functionaltest", "resources");
    private static String propertiesFile = "functional-test.properties";

    private static String firefoxBZip2FileUrlLinux = "";

    private static File tempDownloadDirectory = Files.createTempDir();
    private static FirefoxDriver driver;

    public static FirefoxDriver initFirefoxDriver() throws PropertiesNotLoadedException, OSNotSupportedException {
        if (driver != null) {
            return driver;
        }

        FirefoxOptions firefoxOptions = new FirefoxOptions()
                .setHeadless(true);

        firefoxOptions.setCapability("browser.download.folderList", 2);
        firefoxOptions.setCapability("browser.download.dir", tempDownloadDirectory.getPath());
        firefoxOptions.setCapability("browser.helperApps.neverAsk.saveToDisk", "application/json");

        boolean successfullyLoadedProperties = loadProperties();
        if (!successfullyLoadedProperties) {
            LOGGER.error("Properties was not properly loaded.");
            throw new PropertiesNotLoadedException();
        }

        if (SystemUtils.IS_OS_LINUX) {
            FirefoxBinary firefoxBinary = installFirefoxBinary();
            firefoxOptions.setBinary(firefoxBinary);

            System.setProperty("webdriver.gecko.driver", "src/functionaltest/resources/geckodriver");
        } else if (SystemUtils.IS_OS_WINDOWS) {
            System.setProperty("webdriver.gecko.driver", "src/functionaltest/resources/geckodriver.exe");
        } else {
            LOGGER.error(SystemUtils.OS_NAME + " currently not supported.");
            throw new OSNotSupportedException();
        }

        driver = new FirefoxDriver(firefoxOptions);
        System.out.println("Gecko driver version: " + driver.getCapabilities().getVersion());

        //Make sure all firefox browsers are closed after all tests have finished
        Runtime.getRuntime().addShutdownHook(new Thread(() -> driver.quit()));
        return driver;
    }

    public static File getTempDownloadDirectory() {
        return tempDownloadDirectory;
    }

    public static String getBaseUrl(int randomServerPort) {
        return "http://localhost:" + randomServerPort;
    }

    private static FirefoxBinary installFirefoxBinary() {
        String firefoxBZip2FileNameLinux = FilenameUtils.getName(firefoxBZip2FileUrlLinux);

        String firefoxBZip2FilePath = String.join(
                File.separator, tempDownloadDirectory.getPath(), firefoxBZip2FileNameLinux);
        Utils.downloadFileFromUrlToDestination(firefoxBZip2FileUrlLinux, firefoxBZip2FilePath);
        Utils.extractBZip2InDir(firefoxBZip2FilePath, tempDownloadDirectory.getPath());
        File firefoxBinaryFilePath = new File(
                String.join(File.separator, tempDownloadDirectory.getPath(), "firefox", "firefox"));
        Utils.makeBinFileExecutable(firefoxBinaryFilePath);
        FirefoxBinary firefoxBinary = new FirefoxBinary(firefoxBinaryFilePath);
        return firefoxBinary;
    }
    private static boolean loadProperties() {
        final String propertiesFilePath = String.join(File.separator, propertiesPath, propertiesFile);
        final Properties properties = Utils.getProperties(propertiesFilePath);

        firefoxBZip2FileUrlLinux = properties.getProperty("test.selenium.firefox.BZip2File.url.linux");

        if (StringUtils.isEmpty(firefoxBZip2FileUrlLinux)) {
            LOGGER.error("Failed to load properties, firefoxBZip2FileUrlLinux is not set.");
            return false;
        } else {
            LOGGER.debug("Properties have been loaded.");
            return true;
        }
    }

}
