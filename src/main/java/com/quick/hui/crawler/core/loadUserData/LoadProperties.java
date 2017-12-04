package com.quick.hui.crawler.core.loadUserData;

import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yuanj on 2017/12/1.
 */
public class LoadProperties {

  private static Logger logger = LoggerFactory.getLogger(LoadProperties.class);

  public static Map loadCookieProperties(String path) {
    InputStream is = null;
    Properties p = new Properties();
    Map<String, String> cookieMap = Maps.newHashMap();
    try {
      is = new FileInputStream(new File(path));
      if (Objects.isNull(is)) {
        logger.info("加载cookie文件出错");
        throw new RuntimeException();
      }
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
    logger.info("加载cookie文件成功");
    return cookieMap;
  }

}
