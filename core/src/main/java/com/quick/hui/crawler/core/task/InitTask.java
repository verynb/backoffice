package com.quick.hui.crawler.core.task;

import com.google.common.collect.Maps;
import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.entity.LoginAuthTokenData;
import com.quick.hui.crawler.core.job.CrawJobResult;
import com.quick.hui.crawler.core.utils.GsonUtil;
import com.quick.hui.crawler.core.utils.HttpUtils;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by yuanj on 2017/11/27.
 */
public class InitTask {

  private static String URL = "https://www.bitbackoffice.com";

  private static String RECAPTCHA_URL = URL + "/_Incapsula_Resource?SWCGHOEL=v2";

  private static String INCAPSULA_ERROR = "Request unsuccessful. Incapsula incident ID: 877000090238199605-578629079485186202";

//  private static String RESOURCE_SRC="";

  public static CrawJobResult buildFirstPageTask() {
    Set<String> selectRule = new HashSet<>();
    selectRule.add("iframe[src]");
    CrawlMeta crawlMeta = new CrawlMeta(URL, selectRule);
    CrawJobResult result = new CrawJobResult();
    result.setCrawlMeta(crawlMeta);
    result.getHttpConf().setMethod(HttpMethod.GET);
    return result;
  }

  public static CrawJobResult buildIncapsulaResourceTask(String src) {
    Set<String> selectRule = new HashSet<>();
//    selectRule.add("iframe[src]");
    CrawlMeta crawlMeta = new CrawlMeta(URL + src, selectRule);
    CrawJobResult result = new CrawJobResult();
    result.setCrawlMeta(crawlMeta);
    result.getHttpConf().setMethod(HttpMethod.GET);
    return result;
  }

  public static CrawJobResult buildRecaptchaResourceTask(String reKey) {
    Set<String> selectRule = new HashSet<>();
//    selectRule.add("iframe[src]");
    CrawlMeta crawlMeta = new CrawlMeta(RECAPTCHA_URL, selectRule);
    CrawJobResult result = new CrawJobResult();
    result.setCrawlMeta(crawlMeta);
    result.getHttpConf().setMethod(HttpMethod.POST);
//    result.getHttpConf().getRequestHeaders().put("Content-Type", "application/x-www-form-urlencoded");
//    result.getHttpConf().getRequestParams().put("SWCGHOEL", "v2");
//    result.getHttpConf().getRequestParams().put("g-recaptcha-response", reKey);
    return result;
  }

  public static void reRecaptchaEcecute(String src) throws Exception {
    CrawJobResult result = buildIncapsulaResourceTask(src);
    HttpResponse resourceResponse = HttpUtils
        .request(result.getCrawlMeta(), result.getHttpConf().buildCookie());
    Document resourceDoc = Jsoup.parse(EntityUtils.toString(resourceResponse.getEntity()));

    Element recaptchaKey = resourceDoc.select("div[class=g-recaptcha]").first();
    if (!Objects.isNull(recaptchaKey)) {

      System.out.println("div[class=g-recaptcha]======" + recaptchaKey);

      System.out.println("data-sitekey======" + recaptchaKey.attr("data-sitekey"));
      CrawJobResult recaptchaResult = buildRecaptchaResourceTask(recaptchaKey.attr("data-sitekey"));

//      String jsonParam="{"+"g-recaptcha-response:"+recaptchaKey.attr("data-sitekey")+"}";
      Map<String, String> jsonParam = Maps.newHashMap();
      jsonParam.put("g-recaptcha-response", recaptchaKey.attr("data-sitekey"));
      System.out.println("jsonParam=======>" + GsonUtil.objectTojson(jsonParam));
      HttpResponse recaptchaResponse = HttpUtils
          .doPostJson(recaptchaResult.getCrawlMeta(), recaptchaResult.getHttpConf().buildCookie(),
              GsonUtil.objectTojson(jsonParam));
      if (recaptchaResponse.getStatusLine().getStatusCode() == 200) {
        Thread.sleep(2000);
        execute();
      }
    } else {
      return;
    }
  }

  public static LoginAuthTokenData execute() {
    CrawJobResult result = buildFirstPageTask();
    try {
      HttpResponse response = HttpUtils
          .request(result.getCrawlMeta(), result.getHttpConf().buildCookie());
      Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
      Element element = doc.select("iframe[src]").first();
      if (!Objects.isNull(element)) {
        String src = element.attr("src");
        reRecaptchaEcecute(src);
        return new LoginAuthTokenData(200, element.val());
      } else {
        return new LoginAuthTokenData(400, INCAPSULA_ERROR);
      }
    } catch (Exception e) {
      return new LoginAuthTokenData(500, e.getMessage());
    }
  }


  public static int executeSucess() {
    CrawJobResult result = buildFirstPageTask();
    try {
      HttpResponse response = HttpUtils
          .request(result.getCrawlMeta(), result.getHttpConf().buildCookie());
      Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
      Element element = doc.select("iframe[src]").first();
      if (!Objects.isNull(element)) {
        return 400;
      } else {
        return 200;
      }
    } catch (Exception e) {
      return 500;
    }
  }

}
