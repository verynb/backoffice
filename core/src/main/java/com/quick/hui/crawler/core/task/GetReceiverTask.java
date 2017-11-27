package com.quick.hui.crawler.core.task;

import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.job.CrawJobResult;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yuanj on 2017/11/27.
 */
public class GetReceiverTask {

  private static String URL = "https://www.bitbackoffice.com/users/is_down_line_binary";

  public static CrawJobResult buildTask(String user) {
    Set<String> selectRule = new HashSet<>();
    CrawlMeta crawlMeta = new CrawlMeta(URL,selectRule);
    CrawJobResult result = new CrawJobResult();
    result.setCrawlMeta(crawlMeta) ;
    result.getHttpConf().setMethod(HttpMethod.GET);
    result.getHttpConf().getRequestParams().put("user", user);
    return result;
  }

  public static int getCode(CrawJobResult crawlMeta) {
    return crawlMeta.getCrawlResult().getStatus().getCode();
  }

}
