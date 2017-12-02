package com.scheduled;

import com.quick.hui.crawler.core.entity.TransferUserInfo;
import com.quick.hui.crawler.core.job.SimpleCrawlJob;
import com.quick.hui.crawler.core.loadUserData.LoadData;
import com.quick.hui.crawler.core.loadUserData.LoadProperties;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/12/2.
 */
public class ScheduledThread {
  public static void main(String[] args) {
    Properties config = LoadProperties.loadConfigProperties();
    List<TransferUserInfo> userInfos = LoadData.loadUserInfoData("F:\\backoffice\\account.csv");
    Map<String, String> cookie = LoadProperties.loadCookieProperties();
//初始化5个线程，多个任务，每个任务延迟1s,每隔1个小时执行一次
    ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
    userInfos.forEach(u -> {
      scheduledThreadPool.scheduleAtFixedRate(new SimpleCrawlJob(u,
              null,
              null, cookie),
          1, 3600, TimeUnit.SECONDS);
    });
  }
}
