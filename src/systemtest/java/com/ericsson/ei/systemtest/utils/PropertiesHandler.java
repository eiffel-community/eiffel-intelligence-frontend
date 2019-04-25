package com.ericsson.ei.systemtest.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

public class PropertiesHandler {

    /**
     * Function that set the properties.
     */
    public static void setProperties() throws Throwable {
        final String eiConfigPropertiesFilepath;

        if (System.getProperty("ei.config.properties.file.path") == null || System.getProperty("ei.config.properties.file.path").equals("")) {
            eiConfigPropertiesFilepath = "src/systemtest/resources/system-test.properties";
        } else {
            eiConfigPropertiesFilepath = System.getProperty("ei.config.properties.file.path");
        }

        InputStream systemTestFileInputStream = new FileInputStream(new File(eiConfigPropertiesFilepath));
        Properties systemTestProperties = new Properties();
        systemTestProperties.load(systemTestFileInputStream);
        for (Entry entry : systemTestProperties.entrySet()) {
            String key = entry.getKey()
                              .toString();
            String valueInFile = entry.getValue()
                                      .toString();
            String value = getPrioritizedValue(key, valueInFile);
            System.setProperty(key, value);
        }
    }

    /**
     * Function for to check if system or environment properties exists, if yes then
     * override the value with the preference order property in file < system
     * property < environment property
     *
     * @param key,
     *            property name
     * @param value,property
     *            value from the input file
     *
     * @return property value
     */
    public static String getPrioritizedValue(String key, String value) {
        boolean environmentPropertyExist = (System.getenv(key) != null);
        boolean systemPropertyExist = (System.getProperty(key) != null);

        if (environmentPropertyExist) {
            return System.getenv(key);
        } else if (systemPropertyExist) {
            return System.getProperty(key);
        } else {
            return value;
        }
    }
}
