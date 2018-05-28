package gk.tweetsched.api.util;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

import static gk.tweetsched.api.util.Constants.PROPERTY_FILE;

/**
 * PropertiesManager class.
 * <p>
 * Date: May 28, 2018
 * <p>
 *
 * @author Gleb Kosteiko.
 */
public class PropertiesManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesManager.class);
    private static PropertiesManager instance = null;
    private Properties props = new Properties();

    private PropertiesManager() {
        try {
            loadProps();
        } catch (IOException e) {
            LOGGER.error("PropertyManager initialization is failed", e);
        }
    }

    public static synchronized PropertiesManager getInstance() {
        if (instance == null)
            instance = new PropertiesManager();
        return instance;
    }

    private void loadProps() throws IOException {
        props.load(getClass().getClassLoader().getResourceAsStream(PROPERTY_FILE));
    }

//    public String getProp(String key) {
//        return props.getProperty(key);
//    }
//
//    public String getProp(String key, String defaultValue) {
//        String propertyValue = props.getProperty(key);
//        return propertyValue.isEmpty() ? defaultValue : propertyValue;
//    }
}
