package util;

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
        final String systemTestFilePath;

        if (System.getProperty("propertiesFile") != null) {
            systemTestFilePath = System.getProperty("propertiesFile");
        } else {
            systemTestFilePath = "src/test/resources/system_test.properties";
        }
        InputStream systemTestFileInputStream = new FileInputStream(new File(systemTestFilePath));
        Properties systemTestProperties = new Properties();
        systemTestProperties.load(systemTestFileInputStream);
        for (Entry entry : systemTestProperties.entrySet()) {
            String key = entry.getKey()
                              .toString();
            String valueInFile = entry.getValue()
                                      .toString();
            String value = getPriorityValue(key, valueInFile);
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
    public static String getPriorityValue(String key, String value) {
        if ((System.getProperty(key) != null) || (System.getenv(key) != null)) {
            return (System.getenv(key) != null) ? System.getenv(key) : System.getProperty(key);
        } else {
            return value;
        }
    }
}
