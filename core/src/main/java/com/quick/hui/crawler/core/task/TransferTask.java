package com.quick.hui.crawler.core.task;

import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.job.CrawJobResult;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yuanj on 2017/11/27.
 */
public class TransferTask {

  private static String URL = "https://www.bitbackoffice.com/transfers";

  public static CrawJobResult buildTask(String tokenValue, String userName, String password) {
    Set<String> selectRule = new HashSet<>();
    CrawlMeta crawlMeta = new CrawlMeta(URL,selectRule);
    CrawJobResult result = new CrawJobResult();
    result.setCrawlMeta(crawlMeta) ;
    result.getHttpConf().setMethod(HttpMethod.POST);
    result.getHttpConf().getRequestParams().put("authenticity_token", userName);
    result.getHttpConf().getRequestParams().put("transfer_to", password);
    result.getHttpConf().getRequestParams().put("partition_transfer_partition[user_wallet_id]", tokenValue);
    result.getHttpConf().getRequestParams().put("partition_transfer_partition[amount]", tokenValue);

    result.getHttpConf().getRequestParams().put("partition_transfer_partition[token]", tokenValue);
    result.getHttpConf().getRequestParams().put("partition_transfer_partition[user_id]", tokenValue);
    result.getHttpConf().getRequestParams().put("partition_transfer_partition[receiver_id]", tokenValue);
    result.getHttpConf().getRequestParams().put("partition_transfer_partition[receiver_wallet_id]", tokenValue);
    return result;
  }

  public static int getCode(CrawJobResult crawlMeta) {
    return crawlMeta.getCrawlResult().getStatus().getCode();
  }

}
