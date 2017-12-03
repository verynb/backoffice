package com.scheduled;

import com.quick.hui.crawler.core.entity.TransferUserInfo;
import com.quick.hui.crawler.core.job.SimpleCrawlJob;
import com.quick.hui.crawler.core.loadUserData.LoadData;
import com.quick.hui.crawler.core.loadUserData.LoadProperties;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2017/12/2.
 */
public class ScheduledThread {

  private static Logger logger = LoggerFactory.getLogger(SimpleCrawlJob.class);

  public static void main(String[] args) {
    if (args.length == 0) {
      logger.info("请配置cookie文件与userData文件的正确路径");
      logger.info("eg:cookie=F:\\xxx\\account.csv userData=F:\\xxx\\cookies.csv");
      return;
    } else {
      List<String> argList = Arrays.asList(args);
      Optional<String> cookiePath = argList.stream().filter(s -> s.contains("cookie="))
          .findFirst();
      Optional<String> userDataPath = argList.stream().filter(s -> s.contains("userData="))
          .findFirst();
      if (!cookiePath.isPresent()) {
        logger.info("获取cookie路径失败");
        return;
      }
      if (!userDataPath.isPresent()) {
        logger.info("获取userData路径失败");
        return;
      }
      logger.info("应用启动。。。");
      logger.info("开始加载用户数据");
      List<TransferUserInfo> userInfos = LoadData.loadUserInfoData(userDataPath.get().split("=")[1]);
      logger.info("开始加载cookie数据");
      Map<String, String> cookie = LoadData.loadCookies(cookiePath.get().split("=")[1]);
//初始化5个线程，多个任务，每个任务延迟1s,每隔1个小时执行一次
      ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(50);
      userInfos.forEach(u -> {
        scheduledThreadPool.scheduleAtFixedRate(new SimpleCrawlJob(u,
                null,
                null, cookie),
            1, 2, TimeUnit.SECONDS);
      });
    }
  }
}
