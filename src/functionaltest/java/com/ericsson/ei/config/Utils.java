package com.ericsson.ei.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    /**
     * Loads properties from file path.
     *
     * @param filePath
     *            path to the properties file
     * @return properties object
     */
    public static Properties getProperties(final String filePath) {
        Properties properties = new Properties();
        File file = new File(filePath);
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            LOGGER.error("Failed load properties from path.\nError: {}", e.getMessage());
        }
        return properties;
    }

    /**
     * Extracts a BZip2 archive to a destination folder.
     *
     * @param firefoxTarballFilePath
     *            string containing the firefox BZip2 filepath.
     * @param destination
     *            string containing a destination path.
     */
    public static void extractBZip2InDir(final String firefoxBZip2FilePath, String destinationPath) {
        LOGGER.info("Extracting firefox BZip2 archive...");
        try (TarArchiveInputStream fileInput = new TarArchiveInputStream(
                new BZip2CompressorInputStream(new FileInputStream(firefoxBZip2FilePath)))) {
            TarArchiveEntry entry;
            while ((entry = fileInput.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                final File curfile = new File(destinationPath, entry.getName());
                final File parent = curfile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                final FileOutputStream fileOutput = new FileOutputStream(curfile);
                IOUtils.copy(fileInput, fileOutput);
                fileOutput.close();
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("FileNotFoundException.\nError: {}", e.getMessage());
        } catch (IOException e) {
            LOGGER.error("IOException.\nError: {}", e.getMessage());
        }
    }

    /**
     * Downloads a file from a given URL to a given destination path.
     *
     * @param url
     *            string containing a URL.
     * @param destination
     *            string containing a destination path.
     */
    public static void downloadFileFromUrlToDestination(final String url, final String destination) {
        final File file = new File(destination);
        URL urlObj = null;

        try {
            urlObj = new URL(url);
            LOGGER.info("Downloading file.\nSource: {}\nDestination: {}", url, destination);
            FileUtils.copyURLToFile(urlObj, file);
        } catch (MalformedURLException e) {
            LOGGER.error("Failed to create URL object.\nURL: {}\nError: {}", url, e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Failed to download file.\nURL: {}\nError: {}", url, e.getMessage());
        }
    }

    /**
     * Makes file executable in filesystem.
     *
     * @param binFile
     *            file to make executable
     */
    public static void makeBinFileExecutable(final File binFile) {
        if (binFile.isFile()) {
            LOGGER.info("Changing bin file to be executable.\nPath: {}", binFile.getPath());
            binFile.setExecutable(true);
        } else {
            LOGGER.error("Path is not a file.\nPath: {}", binFile.getPath());
        }
    }
}
