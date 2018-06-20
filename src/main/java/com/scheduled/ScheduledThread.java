package com.scheduled;

import com.google.common.collect.Lists;
import com.quick.hui.crawler.core.entity.ThreadConfig;
import com.quick.hui.crawler.core.entity.ThreadResult;
import com.quick.hui.crawler.core.entity.TransferUserInfo;
import com.quick.hui.crawler.core.job.SimpleCrawlJob;
import com.quick.hui.crawler.core.loadUserData.LoadData;
import com.quick.hui.crawler.core.loadUserData.LoadProperties;
import com.util.IdentityCheck;
import com.util.RandomUtil;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2017/12/2.
 */
public class ScheduledThread {

  private static Logger logger = LoggerFactory.getLogger(ScheduledThread.class);

  private static List<ThreadResult> threadResults = Lists.newArrayList();

  private static String version="1.2";

  public static List<ThreadResult> getThreadResults() {
    return threadResults;
  }

  public static void setThreadResults(List<ThreadResult> threadResults) {
    ScheduledThread.threadResults = threadResults;
  }

  public static String getVersionData(){
    return new DateTime().getMillis()+"-"+version;
  }

  public static void main(String[] args) {
    IdentityCheck.checkVersion(version);
    logger.info("[version="+version+"] ["+new DateTime().toString("yyyy-MM-dd")+"]应用启动。。。");
    logger.info("开始加载用户数据");
    List<TransferUserInfo> userInfos = LoadData.loadUserInfoData("./account.csv");
    ThreadConfig config = LoadProperties.loadConfigProperties("./config.properties");

    ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(config.getThreadPoolSize());
    for(int i=0;i<userInfos.size();i++){
      scheduledThreadPool.schedule(new SimpleCrawlJob(userInfos.get(i),
              config, null),
          5, TimeUnit.SECONDS);
      try {
        int space = RandomUtil.ranNum(config.getThreadspaceTime() * 1000+5000);
        logger.info("任务时间间隔:" + space + "ms");
        Thread.sleep(space);
      } catch (InterruptedException e) {
      }
    }
    scheduledThreadPool.shutdown();

    while (true) {
      if (scheduledThreadPool.isTerminated()) {
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
    long successCount=userInfos.stream().filter(u ->u.getNum()==0).count();
    long failueCount=userInfos.stream().filter(u ->u.getNum()!=0).count();
    System.out.println("所有任务执行完毕，成功："+successCount+",失败："+failueCount);
    System.out.println("输入任意结束");
    Scanner scan = new Scanner(System.in);
    String read = scan.nextLine();
    while (StringUtils.isBlank(read)){

    }
  }
}
