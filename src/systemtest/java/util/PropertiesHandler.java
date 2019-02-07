package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

public class PropertiesHandler {

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
            System.out.println("ValueInFile +++++++++++++++++++++++++++++" + valueInFile);
            String value = getPeriorityValue(key, valueInFile);
            System.out.println("value +++++++++++++++++++++++++++++" + value);
            System.setProperty(key, value);
        }
    }

    public static String getPeriorityValue(String key, String value) {
        if ((System.getProperty(key) != null) || (System.getenv(key) != null)) {
            return (System.getenv(key) != null) ? System.getenv(key) : System.getProperty(key);
        } else {
            return value;
        }
    }
}
