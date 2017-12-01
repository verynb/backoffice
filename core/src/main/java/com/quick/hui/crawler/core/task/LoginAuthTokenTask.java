package com.quick.hui.crawler.core.task;

import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.entity.LoginAuthTokenData;
import com.quick.hui.crawler.core.job.CrawJobResult;
import com.quick.hui.crawler.core.utils.HttpUtils;
import java.util.HashSet;
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
public class LoginAuthTokenTask {

  private static String URL = "https://www.bitbackoffice.com/auth/login";

  private static String INCAPSULA_ERROR = "Request unsuccessful. Incapsula incident ID: 877000090238199605-578629079485186202";

  public static CrawJobResult buildTask() {
    Set<String> selectRule = new HashSet<>();
    selectRule.add("input[name=authenticity_token]");
    CrawlMeta crawlMeta = new CrawlMeta(URL, selectRule);
    CrawJobResult result = new CrawJobResult();
    result.setCrawlMeta(crawlMeta);
    result.getHttpConf().setMethod(HttpMethod.GET);
    return result;
  }

  public static LoginAuthTokenData execute() {
    CrawJobResult result = buildTask();
    LoginAuthTokenData loginAuthTokenData = null;
    try {
      HttpResponse response = HttpUtils
          .request(result.getCrawlMeta(), result.getHttpConf().buildCookie());
      Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
      Element element = doc.select("input[name=authenticity_token]").first();
      if (!Objects.isNull(element)) {
        loginAuthTokenData = new LoginAuthTokenData(200, element.val());
      } else {
        loginAuthTokenData = new LoginAuthTokenData(400, INCAPSULA_ERROR);
      }
    } catch (Exception e) {
      loginAuthTokenData = new LoginAuthTokenData(500, e.getMessage());
    }
    System.out.println("loginAuthTokenData======>" + loginAuthTokenData.toString());
    return loginAuthTokenData;
  }

}
