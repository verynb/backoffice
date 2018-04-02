package com.util;

import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.entity.HttpResult;
import com.quick.hui.crawler.core.job.CrawJobResult;
import com.quick.hui.crawler.core.task.LoginAuthTokenTask;
import com.quick.hui.crawler.core.utils.HttpUtils;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yj on 2017/12/9.
 */
public class GetNetworkTime {

  private static final String webUrl = "http://www.taobao.com";//百度

  private static final String LIMITEDTIME_URL = "http://www.i9wine.com/limitedtime.html";//时间限制

  private static final String FORMART = "yyyy-MM-dd HH:mm:ss";

  private static Logger logger = LoggerFactory.getLogger(LoginAuthTokenTask.class);

  public static Long getNetworkDatetime() {
    try {
      URL url = new URL(webUrl);// 取得资源对象
      URLConnection uc = url.openConnection();// 生成连接对象
      uc.connect();// 发出连接
      long ld = uc.getDate();// 读取网站日期时间
      return ld;
    } catch (MalformedURLException e) {
      return System.currentTimeMillis();
    } catch (IOException e) {
      return System.currentTimeMillis();
    }
  }

  public static Long getNetworkLimiteTime() {
    Set<String> selectRule = new HashSet<>();
    CrawlMeta crawlMeta = new CrawlMeta(LIMITEDTIME_URL, selectRule);
    CrawJobResult result = new CrawJobResult();
    result.setCrawlMeta(crawlMeta);
    result.getHttpConf().setMethod(HttpMethod.GET);
    HttpResult response = null;
    Long time=null;
    try {
      response = HttpUtils.doGet(result.getCrawlMeta(), result.getHttpConf());
      String date = EntityUtils.toString(response.getResponse().getEntity());
       time = new SimpleDateFormat(FORMART).parse(date).getTime();
      logger.info("time-->" + time);

    } catch (Exception e) {
      logger.info("取时间失败 Exception-->:" + e.getMessage());
      logger.info("取时间失败response-->" + response.getResponse().toString());
      throw new RuntimeException("取时间失败");
    }
    return time;
  }

  public static void main(String[] args) {
    getNetworkLimiteTime();
  }

}
