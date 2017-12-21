package com.quick.hui.crawler.core.task;

import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.entity.HttpResult;
import com.quick.hui.crawler.core.entity.UserInfo;
import com.quick.hui.crawler.core.job.CrawJobResult;
import com.quick.hui.crawler.core.utils.GsonUtil;
import com.quick.hui.crawler.core.utils.HttpUtils;
import java.util.HashSet;
import java.util.Set;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yuanj on 2017/11/27.
 */
public class GetReceiverTask {

  private static Logger logger = LoggerFactory.getLogger(GetReceiverTask.class);
  private static String URL = "https://www.bitbackoffice.com/users/is_down_line_binary";

  public static CrawJobResult buildTask(String userName) {
    Set<String> selectRule = new HashSet<>();
    CrawlMeta crawlMeta = new CrawlMeta(URL, selectRule);
    CrawJobResult result = new CrawJobResult();
    result.setCrawlMeta(crawlMeta);
    result.getHttpConf().setMethod(HttpMethod.GET);
    result.getHttpConf().getRequestParams().put("user", userName);
    result.getHttpConf().getRequestHeaders().put("x-requested-with", "XMLHttpRequest");
    return result;
  }

  public static UserInfo execute(String userName) {
    CrawJobResult result = buildTask(userName);
    HttpResult response=null;
    try {
      response = HttpUtils
          .doGet(result.getCrawlMeta(), result.getHttpConf().buildCookie());
      UserInfo userInfo = GsonUtil.jsonToObject(EntityUtils.toString(response.getResponse().getEntity()), UserInfo.class);
      if (!userInfo.getResponse()) {
        logger.error("转账人[" + userName + "]不存在或者不存在于您的二进制树中");
      }
      return userInfo;
    } catch (Exception e) {
      logger.error("获取转账人信息失败" + e.getMessage());
      return null;
    }finally {
      response.getHttpGet().releaseConnection();
      response.getHttpClient().getConnectionManager().shutdown();
    }
  }
}
