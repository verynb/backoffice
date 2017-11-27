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
public class LoginSuccessTask {

  private static String URL = "https://www.bitbackoffice.com";

  public static CrawJobResult buildTask() {
    Set<String> selectRule = new HashSet<>();
    CrawlMeta crawlMeta = new CrawlMeta(URL,selectRule);
    CrawJobResult result = CrawJobResult.builder()
        .crawlMeta(crawlMeta)
        .build();
    result.getHttpConf().setMethod(HttpMethod.GET);
    return result;
  }

}
