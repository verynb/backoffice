package com.quick.hui.crawler.core.job;

import com.google.common.collect.Lists;
import com.quick.hui.crawler.core.entity.CrawlHttpConf;
import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.entity.CrawlResult;
import com.quick.hui.crawler.core.entity.TransferPageData;
import com.quick.hui.crawler.core.entity.TransferWallet;
import com.quick.hui.crawler.core.task.GetReceiverTask;
import com.quick.hui.crawler.core.task.LoginAuthTokenTask;
import com.quick.hui.crawler.core.task.LoginSuccessTask;
import com.quick.hui.crawler.core.task.LoginTask;
import com.quick.hui.crawler.core.task.TransferPageTask;
import com.quick.hui.crawler.core.utils.HttpUtils;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 最简单的一个爬虫任务
 * <p>
 * Created by yihui on 2017/6/27.
 */
@Getter
@Setter
public class SimpleCrawlJob extends AbstractJob {


  private String initCookie = "";

  @Override
  public void beforeRun() {
    initCookie = "visid_incap_796901=azbNT7SxSkKa4sjC0S40DCegGloAAAAAQUIPAAAAAADWPP+CXlYxBIeBB1AVrj8B; _bidsbackoffice_sessions=MkNrNlhlTFREWXpwZW1KbEczKzRBWGsxalgzTXNQaDVtdXgycWtldHBtWFY1VklDS3dwalRMS2JCcnk3WE9Ya3hCL05GempqMlFTS2FSYkt2Y3FrdjJ1UElUa2Z4cVBWYXZpS0pid3NBdzJISHRQSXo3ZWJ5NDY3Qmd5eWsrZDBHbmtCNzhTbm5MdlNRMG1qczB4OUlnPT0tLVhIU3QwdTJGRG5xaGt0Y2tvdXBIS3c9PQ%3D%3D--117fae060e74b05cb8543f74c6f0766e1fc9a1ad; nlbi_796901=xctkCdRXgAa6RyOoLMejiQAAAAAv2Gcb213eCynCkQPD5EQe; incap_ses_533_796901=GZ1hNOZ+91ZWnHpDA5llB0cYHFoAAAAA+FURGDfIlQIFGqi+XvgXkw==";
  }

  /**
   * 执行抓取网页
   */
  @Override
  public void doFetchPage() throws Exception {

    //取登陆页面的authToken
    CrawJobResult authToken = doResult(LoginAuthTokenTask.buildTask());
    Thread.sleep(1000);
    String authTokenValue = LoginAuthTokenTask.getTAuthToken(authToken);
    //登录
    CrawJobResult login = doResult(LoginTask.buildTask(authTokenValue, "xbya003", "xby19800716x"));
    //登录成功
    if (LoginTask.getCode(login) == 302) {
      Thread.sleep(1);
      //登录重定向后的页面
      CrawJobResult loginSuccess = doResult(LoginSuccessTask.buildTask());
      //进入转账页面
      Thread.sleep(1);
      CrawJobResult getTransferPage = doResultTransferPage(TransferPageTask.buildTask());
      CrawJobResult getReceiver=doResult(GetReceiverTask.buildTask("xbya004"));
    }
  }

  private CrawJobResult doResult(CrawJobResult result) throws Exception {
    HttpResponse response = HttpUtils
        .request(result.getCrawlMeta(), result.getHttpConf().buildCookie(this.initCookie));
    String res = EntityUtils.toString(response.getEntity());
    if (response.getStatusLine().getStatusCode() == 200) { // 请求成功
      doParse(result, res);
    } else {
      CrawlResult crawlResult = new CrawlResult();
      crawlResult.setStatus(response.getStatusLine().getStatusCode(),
          response.getStatusLine().getReasonPhrase());
      if (response.getStatusLine().getStatusCode() == 302) {//重定向
        crawlResult.setUrl(response.getFirstHeader("location").getValue());
      } else {
        crawlResult.setUrl(result.getCrawlMeta().getUrl());
      }
      result.setCrawlResult(crawlResult);
    }
    return result;

  }

  private void doParse(CrawJobResult result, String html) {
    Document doc = Jsoup.parse(html);

    System.out.print(html);
    Map<String, List<String>> map = new HashMap<>(result.getCrawlMeta().getSelectorRules().size());
    for (String rule : result.getCrawlMeta().getSelectorRules()) {
      List<String> list = new ArrayList<>();
      for (Element element : doc.select(rule)) {
        list.add(element.val());
      }

      map.put(rule, list);
    }

    CrawlResult crawlResult = new CrawlResult();
    crawlResult.setHtmlDoc(doc);
    crawlResult.setUrl(result.getCrawlMeta().getUrl());
    crawlResult.setResult(map);
    crawlResult.setStatus(CrawlResult.SUCCESS);
    result.setCrawlResult(crawlResult);
  }


  private CrawJobResult doResultTransferPage(CrawJobResult result) throws Exception {
    HttpResponse response = HttpUtils
        .request(result.getCrawlMeta(), result.getHttpConf().buildCookie(this.initCookie));
    String res = EntityUtils.toString(response.getEntity());
    if (response.getStatusLine().getStatusCode() == 200) { // 请求成功
      doParseForTransferPage(result, res);
    } else {
      CrawlResult crawlResult = new CrawlResult();
      crawlResult.setStatus(response.getStatusLine().getStatusCode(),
          response.getStatusLine().getReasonPhrase());
      if (response.getStatusLine().getStatusCode() == 302) {//重定向
        crawlResult.setUrl(response.getFirstHeader("location").getValue());
      } else {
        crawlResult.setUrl(result.getCrawlMeta().getUrl());
      }
      result.setCrawlResult(crawlResult);
    }
    return result;

  }

  //转账页面解析
  private void doParseForTransferPage(CrawJobResult result, String html) {
    Document doc = Jsoup.parse(html);
    System.out.print(html);
    Map<String, List<String>> map = new HashMap<>(result.getCrawlMeta().getSelectorRules().size());
    List<TransferWallet> list = Lists.newArrayList();
    String authToken = "";
    String transferUserId = "";
    for (String rule : result.getCrawlMeta().getSelectorRules()) {
      for (Element element : doc.select(rule)) {
        if (rule.equals("select[name=partition_transfer_partition[user_wallet_id]]")) {
          List<Element> selectChilds = element.children();
          selectChilds
              .stream()
              .filter(c -> StringUtils.isNotBlank(c.val()))
              .forEach(c -> {
                String walletId = c.val();
                Double amount = Double.valueOf(c.text().substring(c.text().indexOf("$")+1, c.text().length()));
                if (amount > 0) {
                  list.add(new TransferWallet(walletId, amount));
                }
              });
        }
        if (rule.equals("input[name=authenticity_token]")) {
          authToken = element.val();
        }
        if (rule.equals("input[name=partition_transfer_partition[user_id]]")) {
          transferUserId = element.val();
        }
      }
    }
    CrawlResult crawlResult = new CrawlResult();
    if (CollectionUtils.isNotEmpty(list)) {
      TransferPageData data = new TransferPageData();
      data.setAuthToken(authToken);
      data.setTransferUserId(transferUserId);
      data.setTransferWallets(list);
      crawlResult.setTransferPageData(data);
    }
    crawlResult.setHtmlDoc(doc);
    crawlResult.setUrl(result.getCrawlMeta().getUrl());
    crawlResult.setResult(map);
    crawlResult.setStatus(CrawlResult.SUCCESS);
    result.setCrawlResult(crawlResult);
  }


}
