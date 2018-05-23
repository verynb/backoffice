package com.quick.hui.crawler.core.task;

import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.entity.HttpResult;
import com.quick.hui.crawler.core.entity.LoginAuthTokenData;
import com.quick.hui.crawler.core.entity.ThreadConfig;
import com.quick.hui.crawler.core.job.CrawJobResult;
import com.quick.hui.crawler.core.utils.HttpUtils;
import com.util.RandomUtil;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yuanj on 2017/11/27.
 */
public class LoginAuthTokenTask {

  private static Logger logger = LoggerFactory.getLogger(LoginAuthTokenTask.class);
  private static String URL = "https://www.bitbackoffice.com/auth/login";

  private static String INCAPSULA_ERROR = "Request unsuccessful. Incapsula incident ID: 877000090238199605-578629079485186202";

  public static CrawJobResult buildTask() {
    Set<String> selectRule = new HashSet<>();
    CrawlMeta crawlMeta = new CrawlMeta(URL, selectRule);
    CrawJobResult result = new CrawJobResult();
    result.setCrawlMeta(crawlMeta);
    result.getHttpConf().setMethod(HttpMethod.GET);
    return result;
  }

  public static LoginAuthTokenData execute() {
    CrawJobResult result = buildTask();
    LoginAuthTokenData loginAuthTokenData = null;
    HttpResult response=null;
    try {
      response = HttpUtils.doGet(result.getCrawlMeta(), result.getHttpConf());
      logger.info("LoginAuthToken request header="+result.getHttpConf().getRequestHeaders().toString());
      Document doc = Jsoup.parse(EntityUtils.toString(response.getResponse().getEntity()));
      Element element = doc.select("input[name=authenticity_token]").first();
      if (!Objects.isNull(element)) {
        loginAuthTokenData = new LoginAuthTokenData(200, element.val());
        logger.info("获取登录页面auth_token成功auth_token:" + loginAuthTokenData.getResult());
      } else {
        loginAuthTokenData = new LoginAuthTokenData(400, INCAPSULA_ERROR);
        logger.info("获取登录页面auth_token失败"+doc.toString());
      }
    } catch (Exception e) {
      logger.info("获取登录页面请求异常" + e.getMessage());
      return new LoginAuthTokenData(500, e.getMessage());
    }finally {
      response.getHttpGet().releaseConnection();
      response.getHttpClient().getConnectionManager().shutdown();
    }
    return loginAuthTokenData;
  }

  public static LoginAuthTokenData tryTimes(ThreadConfig config) {
    try {
      Thread.sleep(RandomUtil.ranNum(config.getRequestSpaceTime()) * 1000 + 5000);
    } catch (InterruptedException e) {
    }
    for (int i = 1; i <= config.getTransferErrorTimes()+2; i++) {
      LoginAuthTokenData loginAuthTokenData = execute();
      if (loginAuthTokenData.getCode() == 200) {
        return loginAuthTokenData;
      } else {
        try {
          Thread.sleep(RandomUtil.ranNum(config.getRequestSpaceTime()) * 1000 + 5000);
        } catch (InterruptedException e) {
        }
        logger.info("获取登录页面请求重试，剩余" + (config.getTransferErrorTimes()+2 - i) + "次");
      }
    }
    return new LoginAuthTokenData(400, INCAPSULA_ERROR);
  }

}
