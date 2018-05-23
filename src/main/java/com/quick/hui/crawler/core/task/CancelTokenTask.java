package com.quick.hui.crawler.core.task;

import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.entity.HttpResult;
import com.quick.hui.crawler.core.job.CrawJobResult;
import com.quick.hui.crawler.core.utils.HttpUtils;
import java.util.HashSet;
import java.util.Set;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yuanj on 2017/11/27.
 */
public class CancelTokenTask {

  private static Logger logger = LoggerFactory.getLogger(CancelTokenTask.class);
  private static String URL = "https://www.bitbackoffice.com/tokens/cancel?token_type=transfer";

  private static CrawJobResult buildTask() {
    Set<String> selectRule = new HashSet<>();
    CrawlMeta crawlMeta = new CrawlMeta(URL, selectRule);
    CrawJobResult result = new CrawJobResult();
    result.setCrawlMeta(crawlMeta);
    result.getHttpConf().setMethod(HttpMethod.GET);
//    result.getHttpConf().getRequestParams().put("user", userName);
    result.getHttpConf().getRequestHeaders().put("x-requested-with", "XMLHttpRequest");
    return result;
  }

  public static String execute() {
    CrawJobResult result = buildTask();
    HttpResult response=null;
    try {
      response = HttpUtils
          .doGet(result.getCrawlMeta(), result.getHttpConf().buildCookie());
      String jsonData=EntityUtils.toString(response.getResponse().getEntity());
      logger.info("取消转账token返回信息="+jsonData);
      return jsonData;
    } catch (Exception e) {
      logger.info("取消转账token失败" + e.getMessage());
    }finally {
      response.getHttpGet().releaseConnection();
      response.getHttpClient().getConnectionManager().shutdown();
    }
    return "unkonw";
  }
}
