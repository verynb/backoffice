package com.quick.hui.crawler.core.loadUserData;

import com.google.common.collect.Maps;
import com.quick.hui.crawler.core.entity.ThreadConfig;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yuanj on 2017/12/1.
 */
public class LoadProperties {

  private static Logger logger = LoggerFactory.getLogger(LoadProperties.class);


  public static ThreadConfig loadConfigProperties(String path) {
    InputStream is = null;
    Properties p = new Properties();
    Map<String, Integer> configMap = Maps.newHashMap();
    try {
      is = new FileInputStream(new File(path));
      if (Objects.isNull(is)) {
        logger.info("加载配置文件出错");
        throw new RuntimeException();
      }
      p.load(is);
      Integer mailSpaceTime = Integer.valueOf(p.get("mail.space.time").toString());
      Integer requestSpaceTime = Integer.valueOf(p.get("request.space.time").toString());
      Integer mailReceiveErrorTimes = Integer.valueOf(p.get("mail.receive.error.times").toString());
      Integer transferErrorTimes = Integer.valueOf(p.get("transfer.error.times").toString());
      Integer threadSpaceTime = Integer.valueOf(p.get("thread.space.time").toString());
      Integer threadPoolSize = Integer.valueOf(p.get("thread.pool.size").toString());
      return new ThreadConfig(mailSpaceTime, requestSpaceTime, mailReceiveErrorTimes, transferErrorTimes,
          threadSpaceTime,threadPoolSize);
    } catch (Exception e) {
      logger.info("加载配置文件出错,请检查config文件格式是否正确");
      System.out.println("输入任意结束:");
      Scanner scan = new Scanner(System.in);
      String read = scan.nextLine();
      while (StringUtils.isBlank(read)) {
      }
      System.exit(0);
    } finally {
      try {
        is.close();
      } catch (IOException e) {
      }
    }
    logger.info("加载配置文件成功");
    return null;
  }

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
    } catch (Exception e) {
      logger.info("加载cookie文件出错,请检查cookies文件格式是否正确");
      System.out.println("输入任意结束:");
      Scanner scan = new Scanner(System.in);
      String read = scan.nextLine();
      while (StringUtils.isBlank(read)) {
      }
      System.exit(0);
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
