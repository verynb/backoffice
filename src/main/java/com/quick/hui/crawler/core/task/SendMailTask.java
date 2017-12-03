package com.quick.hui.crawler.core.task;

import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.entity.SendMailResult;
import com.quick.hui.crawler.core.entity.UserInfo;
import com.quick.hui.crawler.core.job.CrawJobResult;
import com.quick.hui.crawler.core.utils.GsonUtil;
import com.quick.hui.crawler.core.utils.HttpUtils;
import java.util.HashSet;
import java.util.Set;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yuanj on 2017/11/27.
 */
public class SendMailTask {
  private static Logger logger = LoggerFactory.getLogger(SendMailTask.class);
  private static String URL = "https://www.bitbackoffice.com/tokens";

  public static CrawJobResult buildTask(String token,String userId) {
    Set<String> selectRule = new HashSet<>();
    CrawlMeta crawlMeta = new CrawlMeta(URL,selectRule);
    CrawJobResult result = new CrawJobResult();
    result.setCrawlMeta(crawlMeta) ;
    result.getHttpConf().setMethod(HttpMethod.POST);
    result.getHttpConf().getRequestParams().put("authenticity_token", token);
    result.getHttpConf().getRequestParams().put("token[user_id]", userId);
    result.getHttpConf().getRequestParams().put("token[token_type]", "transfer");
    result.getHttpConf().getRequestHeaders().put("x-requested-with","XMLHttpRequest");
    return result;
  }

  public static SendMailResult execute(String token,String userId) {
    CrawJobResult result = buildTask(token,userId);
    try {
      HttpResponse response = HttpUtils
          .request(result.getCrawlMeta(), result.getHttpConf().buildCookie());
      String returnStr=EntityUtils.toString(response.getEntity());
      if(returnStr.contains("number_exceeded")){
        logger.info("线程"+Thread.currentThread().getName()+"拒绝发送邮件，有未使用的邮件");
        return new SendMailResult("success","number_exceeded");
      }else {
        logger.info("线程"+Thread.currentThread().getName()+"发送邮件成功");
        return GsonUtil.jsonToObject(returnStr, SendMailResult.class);
      }
    } catch (Exception e) {
      logger.info("线程"+Thread.currentThread().getName()+"发送邮件请求异常"+e.getMessage());
      return null;
    }
  }

}