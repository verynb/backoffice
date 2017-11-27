package com.quick.hui.crawler.core.task;

import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.job.CrawJobResult;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yuanj on 2017/11/27.
 */
public class TransferPageTask {

  private static String URL = "https://www.bitbackoffice.com/transfers";

  public static CrawJobResult buildTask() {
    Set<String> selectRule = new HashSet<>();
    selectRule.add("input[name=authenticity_token]");
    selectRule.add("select[name=partition_transfer_partition[user_wallet_id]]");
    selectRule.add("input[name=partition_transfer_partition[user_id]]");
    CrawlMeta crawlMeta = new CrawlMeta(URL,selectRule);
    CrawJobResult result = new CrawJobResult();
    result.setCrawlMeta(crawlMeta) ;
    result.getHttpConf().setMethod(HttpMethod.GET);
    return result;
  }

}
