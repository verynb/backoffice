package com.quick.hui.crawler.core.task;

import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.job.CrawJobResult;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yuanj on 2017/11/27.
 */
public class SendMailTask {

  private static String URL = "https://www.bitbackoffice.com/tokens";

  public static CrawJobResult buildTask(String token,String userId) {
    Set<String> selectRule = new HashSet<>();
    CrawlMeta crawlMeta = new CrawlMeta(URL,selectRule);
    CrawJobResult result = new CrawJobResult();
    result.setCrawlMeta(crawlMeta) ;
    result.getHttpConf().setMethod(HttpMethod.POST);
    result.getHttpConf().getRequestParams().put("authenticity_token", token);
    result.getHttpConf().getRequestParams().put("token[user_id]", userId);
    result.getHttpConf().getRequestParams().put("token[token_type]", "transfer");
//    result.getHttpConf().getRequestHeaders().put("Content-Type","application/json;charset=UTF-8");
    result.getHttpConf().getRequestHeaders().put("x-requested-with","XMLHttpRequest");
    return result;
  }

  public static int getCode(CrawJobResult crawlMeta) {
    return crawlMeta.getCrawlResult().getStatus().getCode();
  }

}
