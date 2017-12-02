package com.quick.hui.crawler.core.task;

import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.entity.SendMailResult;
import com.quick.hui.crawler.core.entity.TransferParam;
import com.quick.hui.crawler.core.job.CrawJobResult;
import com.quick.hui.crawler.core.utils.GsonUtil;
import com.quick.hui.crawler.core.utils.HttpUtils;
import java.util.HashSet;
import java.util.Set;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * Created by yuanj on 2017/11/27.
 */
public class TransferTask {

  private static String URL = "https://www.bitbackoffice.com/transfers";

  public static CrawJobResult buildTask(TransferParam param) {
    Set<String> selectRule = new HashSet<>();
    CrawlMeta crawlMeta = new CrawlMeta(URL, selectRule);
    CrawJobResult result = new CrawJobResult();
    result.setCrawlMeta(crawlMeta);
    result.getHttpConf().setMethod(HttpMethod.POST);
    result.getHttpConf().getRequestParams().put("authenticity_token", param.getAuthenticityToken());
    result.getHttpConf().getRequestParams().put("transfer_to", param.getTransferTo());
    result.getHttpConf().getRequestParams()
        .put("partition_transfer_partition[user_wallet_id]", param.getUserWalletId());
    result.getHttpConf().getRequestParams().put("partition_transfer_partition[amount]", param.getAmount());

    result.getHttpConf().getRequestParams()
        .put("partition_transfer_partition[token]", param.getToken());
    result.getHttpConf().getRequestParams()
        .put("partition_transfer_partition[user_id]", param.getUserId());
    result.getHttpConf().getRequestParams()
        .put("partition_transfer_partition[receiver_id]", param.getReceiverId());
    result.getHttpConf().getRequestParams()
        .put("partition_transfer_partition[receiver_wallet_id]", "");
    result.getHttpConf().getRequestHeaders().put("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
    result.getHttpConf().getRequestHeaders().put("referer",URL);
    result.getHttpConf().getRequestHeaders().put("origin","https://www.bitbackoffice.com");
    result.getHttpConf().getRequestHeaders().put("x-requested-with","XMLHttpRequest");
    return result;
  }

  public static int execute(TransferParam param) {
    CrawJobResult result = buildTask(param);
    try {
      HttpResponse response = HttpUtils
          .doPostJson(result.getCrawlMeta(), result.getHttpConf().buildCookie());
      return response.getStatusLine().getStatusCode();
    } catch (Exception e) {
      return 500;
    }
  }

}
