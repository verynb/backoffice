package com.quick.hui.crawler.core.job;

import com.google.common.collect.Lists;
import com.quick.hui.crawler.core.entity.CrawlHttpConf;
import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.entity.CrawlResult;
import com.quick.hui.crawler.core.task.LoginAuthTokenTask;
import com.quick.hui.crawler.core.task.LoginSuccessTask;
import com.quick.hui.crawler.core.task.LoginTask;
import com.quick.hui.crawler.core.task.TransferPageTask;
import com.quick.hui.crawler.core.utils.HttpUtils;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
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
    initCookie = "visid_incap_796901=sIoy1myjQWufx9kokdKeJELUG1oAAAAAQUIPAAAAAABkrQrvSGdjLnjYvXE9IVhc; nlbi_796901=jBzeEDPuZ0dsKWBiLMejiQAAAACvXKT/8W0+MmqgaEAon06j; incap_ses_877_796901=8yPERGlp3Xv9Q6R877orDIbdG1oAAAAAcnzE7rwW/sa67v+BH6+cWw==; _bidsbackoffice_sessions=MzlqNW5UOE9yK1FkcXVFaDNyekhHbHdHbWJSRkp4dUFHbUIySWw3L0lXay8xZGJ6aFZpZjVTdXYvMVorN0dRSGI4SkNKRUwxVXJuUEUrZ3NIU2dGQmJzUjJVeWJNNmNFUENXd2h5bUtEYUtDbDMzWTdrREExYkQ4OVlDOWo3a256UDZSd3ZVYmRFalRLUCtmSkhGZldiTmxyM1REeC9xdDRMR3oyMGdlZ0xZNWd6eXpiMVJNdW1PNXYxckEwVzNXazJkMUVuL2NVdTRzU094eU0zdFJiK05vNlhUSkF3QkNoSzVmcndJdE5TV09PQWxWRXRWaDBwZzFUNlRVU2ZDMi0tQkthVFF5UjRlbUk2TW0ySXMxTE9yUT09--e9ea174de254361a05e1b1489360cd5087d00b76";
  }

  /**
   * 执行抓取网页
   */
  @Override
  public void doFetchPage() throws Exception {

    //取登陆页面的authToken
    CrawJobResult authToken = doResult(LoginAuthTokenTask.buildTask());
    Thread.sleep(1);
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
      CrawJobResult getTransferPage = doResult(TransferPageTask.buildTask());
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

}
