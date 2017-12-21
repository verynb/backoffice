package com.quick.hui.crawler.core.task;

import com.google.common.collect.Lists;
import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.entity.HttpResult;
import com.quick.hui.crawler.core.entity.ThreadConfig;
import com.quick.hui.crawler.core.entity.TransferPageData;
import com.quick.hui.crawler.core.entity.TransferWallet;
import com.quick.hui.crawler.core.job.CrawJobResult;
import com.quick.hui.crawler.core.utils.HttpUtils;
import com.util.RandomUtil;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yuanj on 2017/11/27.
 */
public class TransferPageTask {
  private static Logger logger = LoggerFactory.getLogger(TransferPageTask.class);
  private static String URL = "https://www.bitbackoffice.com/transfers";

  public static CrawJobResult buildTask() {
    Set<String> selectRule = new HashSet<>();
    CrawlMeta crawlMeta = new CrawlMeta(URL, selectRule);
    CrawJobResult result = new CrawJobResult();
    result.setCrawlMeta(crawlMeta);
    result.getHttpConf().setMethod(HttpMethod.GET);
    return result;
  }

  public static TransferPageData execute() {
    CrawJobResult result = buildTask();
    HttpResult response=null;
    try {
      response = HttpUtils
          .doGet(result.getCrawlMeta(), result.getHttpConf().buildCookie());
      Document doc = Jsoup.parse(EntityUtils.toString(response.getResponse().getEntity()));

      Element walletElement = doc.select("select[name=partition_transfer_partition[user_wallet_id]]").first();

      if(Objects.isNull(walletElement)){
        logger.info("未获取到转账页面数据");
        return new TransferPageData("", "", Lists.newArrayList());
      }
      List<TransferWallet> transferWallets = walletElement.children().stream()
          .filter(e -> StringUtils.isNotBlank(e.val()))
          .map(e -> {
            String walletId = e.val();
            Double amount = Double.valueOf(e.text().substring(e.text().indexOf("$") + 1, e.text().length()));
            return new TransferWallet(walletId, amount);
          }).collect(Collectors.toList());

      Element authTokenElement = doc.select("input[name=authenticity_token]").first();

      Element transferUserIdElement = doc.select("input[name=partition_transfer_partition[user_id]]").first();
      logger.info("获取到转账页面数据成功,返回数据如下:");
      logger.info("authToken="+authTokenElement.val());
      logger.info("transferUserId="+transferUserIdElement.val());
      logger.info("transferWallets="+transferWallets.toString());
      return new TransferPageData(authTokenElement.val(), transferUserIdElement.val(), transferWallets);

    } catch (Exception e) {
      logger.info("获取到转账页面请求异常"+e.getMessage());
      return new TransferPageData("", "", Lists.newArrayList());
    }finally {
      response.getHttpGet().releaseConnection();
      response.getHttpClient().getConnectionManager().shutdown();
    }
  }

  public static TransferPageData tryTimes(ThreadConfig config) {
    try {
      Thread.sleep(RandomUtil.ranNum(config.getRequestSpaceTime()) * 1000+5000);
    } catch (InterruptedException e) {
    }
    for (int i = 1; i <= config.getTransferErrorTimes(); i++) {
      TransferPageData code = execute();
      if (CollectionUtils.isNotEmpty(code.getTransferWallets())) {
        return code;
      }else {
        try {
          Thread.sleep(RandomUtil.ranNum(config.getRequestSpaceTime()) * 1000+5000);
        } catch (InterruptedException e) {
        }
        logger.info("获取登录页面请求重试，剩余"+(config.getTransferErrorTimes()-i)+"次");
      }
    }
    return new TransferPageData("", "", Lists.newArrayList());
  }

}
