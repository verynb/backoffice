package com.scheduled;

import com.google.common.collect.Lists;
import com.quick.hui.crawler.core.entity.ThreadResult;
import com.quick.hui.crawler.core.entity.TransferUserInfo;
import com.quick.hui.crawler.core.job.SimpleCrawlJob;
import com.quick.hui.crawler.core.loadUserData.LoadData;
import com.quick.hui.crawler.core.loadUserData.LoadProperties;
import java.util.List;
import java.util.Map;
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
  private static List<ThreadResult> threadResults = Lists.newArrayList();

  public static List<ThreadResult> getThreadResults() {
    return threadResults;
  }

  public static void setThreadResults(List<ThreadResult> threadResults) {
    ScheduledThread.threadResults = threadResults;
  }

  public static void main(String[] args) {
    logger.info("应用启动。。。");
    logger.info("开始加载用户数据");
    List<TransferUserInfo> userInfos = LoadData.loadUserInfoData("./account.csv");
    logger.info("开始加载cookie数据");
    Map<String, String> cookie = LoadProperties.loadCookieProperties("./cookies.properties");
//初始化5个线程，多个任务，每个任务延迟1s,每隔1个小时执行一次
    ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(50);
    userInfos.forEach(u -> {
      scheduledThreadPool.schedule(new SimpleCrawlJob(u,
              null,
              null, cookie),
          1, TimeUnit.SECONDS);
    });
    scheduledThreadPool.shutdown();

    while(true){
      if(scheduledThreadPool.isTerminated()){
        System.out.println("所有的子线程都结束了！");
        break;
      }
//      Thread.sleep(1000);
    }
    System.out.println("==================end===================="+threadResults.toString());

  }
}
