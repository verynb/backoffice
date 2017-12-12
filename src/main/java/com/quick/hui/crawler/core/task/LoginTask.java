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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yuanj on 2017/11/27.
 */
public class LoginTask {

  private static Logger logger = LoggerFactory.getLogger(LoginTask.class);
  private static String URL = "https://www.bitbackoffice.com/auth/login";

  public static CrawJobResult buildTask(String tokenValue, String userName, String password) {
    Set<String> selectRule = new HashSet<>();
    CrawlMeta crawlMeta = new CrawlMeta(URL, selectRule);
    CrawJobResult result = new CrawJobResult();
    result.setCrawlMeta(crawlMeta);
    result.getHttpConf().setMethod(HttpMethod.POST);
    result.getHttpConf().getRequestParams().put("user[username]", userName);
    result.getHttpConf().getRequestParams().put("user[password]", password);
    result.getHttpConf().getRequestParams().put("authenticity_token", tokenValue);
    return result;
  }

  public static int execute(String tokenValue, String userName, String password) {
    logger.info("登录参数{tokenValue="+tokenValue+",userName="+userName+", password="+password+"}");
    CrawJobResult result = buildTask(tokenValue, userName, password);
    try {
      HttpResponse response = HttpUtils
          .request(result.getCrawlMeta(), result.getHttpConf().buildCookie());
      if(response.getStatusLine().getStatusCode()!=302){
        //用户名密码错误
        logger.info("登录失败responseCode:" + response.getStatusLine().getStatusCode());
        return 400;
      }
      logger.info("登录成功responseCode:" + response.getStatusLine().getStatusCode());
      return response.getStatusLine().getStatusCode();
    } catch (Exception e) {
      logger.error("登录请求异常"+e.getMessage());
      return 500;
    }

  }
  /*public static void main(String args[]){
    LoginTask.execute("1","1","1");
  }*/

}
