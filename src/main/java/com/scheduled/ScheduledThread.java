package com.scheduled;

import com.google.common.collect.Lists;
import com.quick.hui.crawler.core.entity.ThreadConfig;
import com.quick.hui.crawler.core.entity.ThreadResult;
import com.quick.hui.crawler.core.entity.TransferUserInfo;
import com.quick.hui.crawler.core.job.SimpleCrawlJob;
import com.quick.hui.crawler.core.loadUserData.LoadData;
import com.quick.hui.crawler.core.loadUserData.LoadProperties;
import com.util.RandomUtil;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    ThreadConfig config = LoadProperties.loadConfigProperties("./config.properties");
    ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(config.getThreadPoolSize());
//    userInfos.forEach(u -> {
    for(int i=0;i<userInfos.size();i++){

      scheduledThreadPool.schedule(new SimpleCrawlJob(userInfos.get(i),
              config, cookie),
          1, TimeUnit.SECONDS);
      try {
        int space = RandomUtil.ranNum(config.getThreadspaceTime() * 1000);
        logger.info("任务时间间隔:" + space + "ms");
        Thread.sleep(space);
      } catch (InterruptedException e) {
      }
    }
    scheduledThreadPool.shutdown();

    while (true) {
      if (scheduledThreadPool.isTerminated()) {
//        System.out.println("所有的子线程都结束了！");
        break;
      }
    }
    userInfos.stream()
        .forEach(user -> {

          Optional<ThreadResult> tr = threadResults.stream().filter(t -> t.getRow() == user.getRow())
              .findFirst();
          if (tr.isPresent()) {
            user.setNum(tr.get().getSuccess() ? 0 : (user.getNum() == null ? 0 : user.getNum()) + 1);
          } else {
            user.setNum((user.getNum() == null ? 0 : user.getNum()) + 1);
          }
        });
    LoadData.writeResult(userInfos);
  }
}
