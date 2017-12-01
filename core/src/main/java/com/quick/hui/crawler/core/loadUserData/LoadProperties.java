package com.quick.hui.crawler.core.loadUserData;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Created by yuanj on 2017/12/1.
 */
public class LoadProperties {

  public static Properties loadConfigProperties() {
    InputStream is = LoadProperties.class.getClassLoader()
        .getResourceAsStream("./config.properties");
    Properties p = new Properties();
    try {
      p.load(is);
    } catch (IOException e) {

    } finally {
      try {
        is.close();
      } catch (IOException e) {
      }
    }
    return p;
  }

  public static Map loadCookieProperties() {
    InputStream is = LoadProperties.class.getClassLoader()
        .getResourceAsStream("./cookie.properties");
    Properties p = new Properties();
    Map<String, String> cookieMap = Maps.newHashMap();
    try {
      p.load(is);
      p.forEach((k, v) -> {
        cookieMap.put(k.toString(), v.toString());
      });
    } catch (IOException e) {

    } finally {
      try {
        is.close();
      } catch (IOException e) {
      }
    }
    return cookieMap;
  }

}
