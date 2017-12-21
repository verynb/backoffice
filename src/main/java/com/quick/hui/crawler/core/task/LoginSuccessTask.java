package com.quick.hui.crawler.core.task;

import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.entity.HttpResult;
import com.quick.hui.crawler.core.job.CrawJobResult;
import com.quick.hui.crawler.core.utils.HttpUtils;
import java.util.HashSet;
import java.util.Set;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yuanj on 2017/11/27.
 */
public class LoginSuccessTask {

  private static Logger logger = LoggerFactory.getLogger(LoginSuccessTask.class);
  private static String URL = "https://www.bitbackoffice.com";

  public static CrawJobResult buildTask() {
    Set<String> selectRule = new HashSet<>();
    CrawlMeta crawlMeta = new CrawlMeta(URL, selectRule);
    CrawJobResult result = new CrawJobResult();
    result.setCrawlMeta(crawlMeta);
    result.getHttpConf().setMethod(HttpMethod.GET);
    return result;
  }

  public static int execute() {
    CrawJobResult result = buildTask();
    HttpResult response=null;
    try {
      response = HttpUtils
          .doGet(result.getCrawlMeta(), result.getHttpConf().buildCookie());
      logger.info("线程"+Thread.currentThread().getName()+"登录后获取主页成功responseCode:" + response.getResponse().getStatusLine().getStatusCode());
      return response.getResponse().getStatusLine().getStatusCode();
    } catch (Exception e) {
      logger.error("线程"+Thread.currentThread().getName()+"登录后获取主页请求异常"+e.getMessage());
      return 500;
    }finally {
      response.getHttpGet().releaseConnection();
      response.getHttpClient().getConnectionManager().shutdown();
    }
  }
}
