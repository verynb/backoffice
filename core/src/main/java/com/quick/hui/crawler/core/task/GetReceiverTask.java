package com.quick.hui.crawler.core.task;

import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
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
    try {
      HttpResponse response = HttpUtils
          .request(result.getCrawlMeta(), result.getHttpConf().buildCookie());
      return GsonUtil.jsonToObject(EntityUtils.toString(response.getEntity()), UserInfo.class);
    } catch (Exception e) {
      logger.error("线程"+Thread.currentThread().getName()+"获取转账人信息失败"+e.getMessage());
      return null;
    }
  }
}
