package com.quick.hui.crawler.core.task;

import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.job.CrawJobResult;
import com.quick.hui.crawler.core.utils.HttpUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by yuanj on 2017/11/27.
 */
public class LoginSuccessTask {

  private static String URL = "https://www.bitbackoffice.com";

  public static CrawJobResult buildTask() {
    Set<String> selectRule = new HashSet<>();
    CrawlMeta crawlMeta = new CrawlMeta(URL, selectRule);
    CrawJobResult result = new CrawJobResult();
    result.setCrawlMeta(crawlMeta);
    result.getHttpConf().setMethod(HttpMethod.GET);
    return result;
  }

  public static int execute() {
    CrawJobResult result = buildTask();
    try {
      HttpResponse response = HttpUtils
          .request(result.getCrawlMeta(), result.getHttpConf().buildCookie());
      return response.getStatusLine().getStatusCode();
    } catch (Exception e) {
      return 500;
    }
  }
  }
