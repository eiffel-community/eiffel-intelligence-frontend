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
            InputStream fis = new FileInputStream(new File(systemTestFilePath));
            Properties prop = new Properties();
            prop.load(fis);
            for (Entry entry : prop.entrySet()) {
                String key = entry.getKey()
                                  .toString();
                String value = entry.getValue()
                                    .toString();
                System.setProperty(key, value);
            }
        }
    }
}
