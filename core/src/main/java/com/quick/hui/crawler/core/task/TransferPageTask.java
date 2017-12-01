package com.quick.hui.crawler.core.task;

import com.google.common.collect.Lists;
import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.entity.TransferPageData;
import com.quick.hui.crawler.core.entity.TransferWallet;
import com.quick.hui.crawler.core.job.CrawJobResult;
import com.quick.hui.crawler.core.utils.HttpUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by yuanj on 2017/11/27.
 */
public class TransferPageTask {

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
    try {
      HttpResponse response = HttpUtils
          .request(result.getCrawlMeta(), result.getHttpConf().buildCookie());
      Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));

      Element walletElement = doc.select("select[name=partition_transfer_partition[user_wallet_id]]").first();

      if(Objects.isNull(walletElement)){
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

      return new TransferPageData(authTokenElement.val(), transferUserIdElement.val(), transferWallets);

    } catch (Exception e) {
      return new TransferPageData("", "", Lists.newArrayList());
    }
  }

}
