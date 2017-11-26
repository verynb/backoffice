package com.quick.hui.crawler.core.job;

import com.google.common.collect.Lists;
import com.quick.hui.crawler.core.entity.CrawlHttpConf;
import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.entity.CrawlResult;
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

  /**
   * 执行抓取网页
   */
  public void doFetchPage() throws Exception {

    String url2 = "https://www.bitbackoffice.com/auth/login";
    Set<String> selectRule2 = new HashSet<>();
    selectRule2.add("input[name=authenticity_token]"); // 博客正文
    CrawlMeta crawlMeta2 = new CrawlMeta();
    crawlMeta2.setUrl(url2);
    crawlMeta2.setSelectorRules(selectRule2);

    CrawJobResult result2 = new CrawJobResult();
    result2.getHttpConf().setMethod(HttpMethod.GET);
    result2.setCrawlMeta(crawlMeta2);
    result2 = doResult(result2);

    String url = "https://www.bitbackoffice.com/auth/login";
    Set<String> selectRule = new HashSet<>();
    selectRule.add("div[class=title]"); // 博客标题
    selectRule.add("div[class=blog-body]"); // 博客正文
    CrawlMeta crawlMeta = new CrawlMeta();
    crawlMeta.setUrl(url);
    crawlMeta.setSelectorRules(selectRule);
    CrawJobResult result = new CrawJobResult();
    result.getHttpConf().setMethod(HttpMethod.POST);
    result.setCrawlMeta(crawlMeta);

    String authToken = result2.getCrawlResult().getResult().get("input[name=authenticity_token]")
        .get(0);
    result.getHttpConf().getRequestParams().put("user[username]", "xbya003");
    result.getHttpConf().getRequestParams().put("user[password]", "xby19800716x");
    result.getHttpConf().getRequestParams().put("authenticity_token", authToken);
    result = doResult(result);

    if(result.getCrawlResult().getStatus().getCode()==302){
      String url3 = "https://www.bitbackoffice.com/";
      Set<String> selectRule3 = new HashSet<>();
      selectRule3.add("div[class=title]"); // 博客标题
      selectRule3.add("div[class=blog-body]"); // 博客正文
      CrawlMeta crawlMeta3 = new CrawlMeta();
      crawlMeta3.setUrl(url3);
      crawlMeta3.setSelectorRules(selectRule);
      CrawJobResult result3 = new CrawJobResult();
      result3.getHttpConf().setMethod(HttpMethod.GET);
      result3.setCrawlMeta(crawlMeta);
      result3 = doResult(result3);
    }
  }

  private CrawJobResult doResult(CrawJobResult result) throws Exception {
    HttpResponse response = HttpUtils
        .request(result.getCrawlMeta(), result.getHttpConf().buildCookie());
    String res = EntityUtils.toString(response.getEntity());
    if (response.getStatusLine().getStatusCode() == 200) { // 请求成功
      doParse(result, res);
    } else {
      CrawlResult crawlResult = new CrawlResult();
      crawlResult.setStatus(response.getStatusLine().getStatusCode(),
          response.getStatusLine().getReasonPhrase());
      if(response.getStatusLine().getStatusCode()==302){//重定向
        crawlResult.setUrl(response.getFirstHeader("location").getValue());
      }else {
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
