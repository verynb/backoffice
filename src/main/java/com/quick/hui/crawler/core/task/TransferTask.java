package com.quick.hui.crawler.core.task;

import static sun.security.pkcs.PKCS8Key.version;

import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.entity.HttpPostResult;
import com.quick.hui.crawler.core.entity.SendMailResult;
import com.quick.hui.crawler.core.entity.TransferParam;
import com.quick.hui.crawler.core.entity.TransferResult;
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
public class TransferTask {

  private static Logger logger = LoggerFactory.getLogger(TransferTask.class);
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
    result.getHttpConf().getRequestHeaders().put("referer", URL);
    result.getHttpConf().getRequestHeaders().put("origin", "https://www.bitbackoffice.com");
    result.getHttpConf().getRequestHeaders().put("x-requested-with", "XMLHttpRequest");
    return result;
  }

  public static TransferResult execute(TransferParam param) {
    CrawJobResult result = buildTask(param);
    HttpPostResult response = null;
    try {
      response = HttpUtils
          .doPostJson(result.getCrawlMeta(), result.getHttpConf().buildCookie());
      String returnStr = EntityUtils.toString(response.getResponse().getEntity());
      logger.info("转账服务器返回:" +returnStr);
      if (returnStr.contains("invalid_token")) {
        logger.info("转账token:" + param.getToken() + "不正确");
        return new TransferResult("error", "invalid_token");
      } else if (returnStr.contains("success")) {
        logger.info("转账成功");
        return GsonUtil.jsonToObject(returnStr, TransferResult.class);
      } else {
        logger.info("未知错误");
        return new TransferResult("error", "unkown");
      }
    } catch (Exception e) {
      logger.info("转账请求异常:" + e.getMessage());
      return new TransferResult("error", "500");
    } finally {
      response.getHttpPost().releaseConnection();
      response.getHttpClient().getConnectionManager().shutdown();
      logger.info("释放连接");
    }
  }
}
