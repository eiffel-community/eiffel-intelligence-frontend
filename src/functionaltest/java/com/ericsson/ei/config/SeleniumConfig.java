package com.ericsson.ei.config;

import java.io.File;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.ei.frontend.exception.OSNotSupportedException;
import com.ericsson.ei.frontend.exception.PropertiesNotLoadedException;
import com.google.common.io.Files;

public class SeleniumConfig {
    private static final FirefoxDriverLogLevel SELENIUM_LOG_LEVEL = FirefoxDriverLogLevel.ERROR;
    private static final boolean SELENIUM_HEADLESS = true;

    private static final Logger LOGGER = LoggerFactory.getLogger(SeleniumConfig.class);

    private static String propertiesPath = String.join(File.separator, "src", "functionaltest",
            "resources");
    private static String propertiesFile = "functional-test.properties";

    private static File tempDownloadDirectory = Files.createTempDir();
    private static FirefoxDriver driver;

    public static FirefoxDriver initFirefoxDriver()
            throws PropertiesNotLoadedException, OSNotSupportedException {
        FirefoxOptions firefoxOptions = new FirefoxOptions()
                                                            .setHeadless(SELENIUM_HEADLESS)
                                                            .setLogLevel(SELENIUM_LOG_LEVEL);

        firefoxOptions.addPreference("browser.download.folderList", 2);
        firefoxOptions.addPreference("browser.download.dir", tempDownloadDirectory.getPath());
        firefoxOptions.addPreference("browser.helperApps.neverAsk.saveToDisk", "application/json");

        if (SystemUtils.IS_OS_LINUX) {
            FirefoxBinary firefoxBinary = getFirefoxBinary();
            firefoxOptions.setBinary(firefoxBinary);

            System.setProperty("webdriver.gecko.driver",
                    "src/functionaltest/resources/geckodriver");
        } else if (SystemUtils.IS_OS_WINDOWS) {
            System.setProperty("webdriver.gecko.driver",
                    "src/functionaltest/resources/geckodriver.exe");
        } else {
            LOGGER.error(SystemUtils.OS_NAME + " currently not supported.");
            throw new OSNotSupportedException();
        }

        driver = new FirefoxDriver(firefoxOptions);

        // Make sure all firefox browsers are closed after all tests have finished
        Runtime.getRuntime().addShutdownHook(new Thread(() -> driver.quit()));
        return driver;
    }

    public static File getTempDownloadDirectory() {
        return tempDownloadDirectory;
    }

    public static String getBaseUrl(int randomServerPort) {
        return "http://localhost:" + randomServerPort;
    }

    private static FirefoxBinary getFirefoxBinary() {
        // Firefox binary will be stored in <repository>/target/firefox/firefox/<binary>
        String firefoxPath = getFirefoxDirPath().getPath();
        File firefoxBinaryFilePath = new File(
                String.join(File.separator, firefoxPath, "firefox", "firefox"));

        if (firefoxBinaryFilePath.isFile()) {
            LOGGER.debug("Reusing existing firefox binary.");
            return new FirefoxBinary(firefoxBinaryFilePath);
        }

        LOGGER.debug("Downloading and extracting new Firefox binary.");
        final String firefoxTarFileUrl = getFirefoxTarFileUrl();
        String firefoxBZip2FileNameLinux = FilenameUtils.getName(firefoxTarFileUrl);
        String firefoxTarFilePath = String.join(
                File.separator, firefoxPath, firefoxBZip2FileNameLinux);
        Utils.downloadFileFromUrlToDestination(firefoxTarFileUrl, firefoxTarFilePath);
        Utils.extractBZip2InDir(firefoxTarFilePath, firefoxPath);
        Utils.makeBinFileExecutable(firefoxBinaryFilePath);

        return new FirefoxBinary(firefoxBinaryFilePath);
    }

    private static File getFirefoxDirPath() {
        String relPath = SeleniumConfig.class.getProtectionDomain()
                                             .getCodeSource()
                                             .getLocation()
                                             .getFile();
        File firefoxDir = new File(relPath + "../firefox");
        if (!firefoxDir.exists()) {
            firefoxDir.mkdir();
        }
        return firefoxDir;
    }

    private static String getFirefoxTarFileUrl() {
        final String propertiesFilePath = String.join(
                File.separator, propertiesPath, propertiesFile);
        final Properties properties = Utils.getProperties(propertiesFilePath);
        final String firefoxTarFileUrl = properties.getProperty(
                "test.selenium.firefox.TarFile.url.linux");
        if (StringUtils.isEmpty(firefoxTarFileUrl)) {
            final String message = "Failed to load firefox binary URL from properties.";
            LOGGER.error(message);
            throw new PropertiesNotLoadedException(message);
        }

        return firefoxTarFileUrl;
    }

}
