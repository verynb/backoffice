package com.quick.hui.crawler.core.task;

import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.job.CrawJobResult;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;

/**
 * Created by yuanj on 2017/11/27.
 */
public class LoginTask {

  private static String URL = "https://www.bitbackoffice.com/auth/login";

  public static CrawJobResult buildTask(String tokenValue, String userName, String password) {
    Set<String> selectRule = new HashSet<>();
    CrawlMeta crawlMeta = new CrawlMeta(URL,selectRule);
    CrawJobResult result = new CrawJobResult();
    result.setCrawlMeta(crawlMeta) ;
    result.getHttpConf().setMethod(HttpMethod.POST);
    result.getHttpConf().getRequestParams().put("user[username]", userName);
    result.getHttpConf().getRequestParams().put("user[password]", password);
    result.getHttpConf().getRequestParams().put("authenticity_token", tokenValue);
    return result;
  }

  public static int getCode(CrawJobResult crawlMeta) {
    return crawlMeta.getCrawlResult().getStatus().getCode();
  }

}
