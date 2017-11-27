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
public class LoginAuthTokenTask {

  private static String URL = "https://www.bitbackoffice.com/auth/login";

  public static CrawJobResult buildTask() {
    Set<String> selectRule = new HashSet<>();
    selectRule.add("input[name=authenticity_token]");
    CrawlMeta crawlMeta = new CrawlMeta(URL,selectRule);
    CrawJobResult result = new CrawJobResult();
    result.setCrawlMeta(crawlMeta) ;
    result.getHttpConf().setMethod(HttpMethod.GET);
    return result;
  }

  public static String getTAuthToken(CrawJobResult crawlMeta) {
    List<String> values = crawlMeta.getCrawlResult().getResult().get("input[name=authenticity_token]");
    if (CollectionUtils.isEmpty(values)) {
      return "";
    } else {
      return values.get(0);
    }
  }


}
