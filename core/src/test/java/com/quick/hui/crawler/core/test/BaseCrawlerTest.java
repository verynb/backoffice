package com.quick.hui.crawler.core.test;

import com.quick.hui.crawler.core.entity.TransferUserInfo;
import com.quick.hui.crawler.core.job.SimpleCrawlJob;
import com.quick.hui.crawler.core.loadUserData.LoadData;
import com.quick.hui.crawler.core.loadUserData.LoadProperties;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.junit.Test;

/**
 *
 */
public class BaseCrawlerTest {

  @Test
  public void testFetch() throws InterruptedException {
    Properties p = LoadProperties.loadConfigProperties();
    List<TransferUserInfo> userInfos = LoadData.loadUserInfoData("F:\\backoffice\\account.csv");
    Map<String, String> cookie = LoadProperties.loadCookieProperties();
//    userInfos.forEach(u -> {
      SimpleCrawlJob job = new SimpleCrawlJob(userInfos.get(0), null, null, cookie);
      Thread thread = new Thread(job, "bit-test");
      thread.start();
      thread.join();
//    });
  }
}
