package org.example.util;

import java.io.IOException;
import java.util.Properties;

public class AppUtil {

  public static Properties getApplicationProperties() {
    Properties properties = new Properties();
    var stream = AppUtil.class.getClassLoader().getResourceAsStream("application.properties");
    if (stream != null) {
      try {
        properties.load(stream);
        return properties;
      } catch (IOException e) {
        throw new IllegalArgumentException(e.getMessage());
      }
    }
    throw new IllegalArgumentException("application.properties is missed!");
  }
}

