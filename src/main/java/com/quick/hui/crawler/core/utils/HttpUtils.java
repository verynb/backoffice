package com.quick.hui.crawler.core.utils;

import com.quick.hui.crawler.core.entity.CrawlHttpConf;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.localSession.LocalCookie;
import com.quick.hui.crawler.core.localSession.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

/**
 * Created by yj on 2017/11/27.
 */
@Slf4j
public class HttpUtils {

  public static HttpResponse request(CrawlMeta crawlMeta, CrawlHttpConf httpConf) throws Exception {
    switch (httpConf.getMethod()) {
      case GET:
        return doGet(crawlMeta, httpConf);
      case POST:
        return doPost(crawlMeta, httpConf);
      default:
        return null;
    }
  }

  /**
   * 执行GET 请求
   */
  private static HttpResponse doGet(CrawlMeta crawlMeta, CrawlHttpConf httpConf) throws Exception {

    CookieStore cookieStore = new BasicCookieStore();

    HttpClient httpClient = HttpClients
        .custom()
        .setDefaultCookieStore(cookieStore)
        .build();

    // 设置请求参数
    StringBuilder param = new StringBuilder(crawlMeta.getUrl()).append("?");
    for (Map.Entry<String, Object> entry : httpConf.getRequestParams().entrySet()) {
      param.append(entry.getKey())
          .append("=")
          .append(entry.getValue())
          .append("&");
    }

    HttpGet httpGet = new HttpGet(param.substring(0, param.length() - 1)); // 过滤掉最后一个无效字符

    // 设置请求头
    for (Map.Entry<String, String> head : httpConf.getRequestHeaders().entrySet()) {
      httpGet.addHeader(head.getKey(), head.getValue());
    }
    // 执行网络请求
    HttpResponse response = httpClient.execute(httpGet);
    //保存cookie
    writeSession(cookieStore.getCookies());
    return response;
  }


  private static HttpResponse doPost(CrawlMeta crawlMeta, CrawlHttpConf httpConf) throws Exception {
    CookieStore cookieStore = new BasicCookieStore();
    HttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    HttpPost httpPost = new HttpPost(crawlMeta.getUrl());
    // 建立一个NameValuePair数组，用于存储欲传送的参数
    List<NameValuePair> params = new ArrayList<>();
    for (Map.Entry<String, Object> param : httpConf.getRequestParams().entrySet()) {
      params.add(new BasicNameValuePair(param.getKey(), param.getValue().toString()));
    }
    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
    // 设置请求头
    for (Map.Entry<String, String> head : httpConf.getRequestHeaders().entrySet()) {
      httpPost.addHeader(head.getKey(), head.getValue());
    }
    HttpResponse response = httpClient.execute(httpPost);
    writeSession(cookieStore.getCookies());
    return response;
  }


  public static HttpResponse doPostJson(CrawlMeta crawlMeta, CrawlHttpConf httpConf)
      throws Exception {
    CookieStore cookieStore = new BasicCookieStore();
    HttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    HttpPost httpPost = new HttpPost(crawlMeta.getUrl());
    // 建立一个NameValuePair数组，用于存储欲传送的参数
    List<NameValuePair> params = new ArrayList<>();
    for (Map.Entry<String, Object> param : httpConf.getRequestParams().entrySet()) {
      params.add(new BasicNameValuePair(param.getKey(), param.getValue().toString()));
    }
    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
    // 设置请求头
    for (Map.Entry<String, String> head : httpConf.getRequestHeaders().entrySet()) {
      httpPost.setHeader(head.getKey(), head.getValue());
    }
    HttpResponse response = httpClient.execute(httpPost);
    writeSession(cookieStore.getCookies());
    return response;
  }


  private static void writeSession(List<Cookie> cookies) {
    List<LocalCookie> localCookies = cookies
        .stream()
        .map(c -> {
          return new LocalCookie(c.getName(), c.getValue());
        }).collect(Collectors.toList());
    Session.writeSession(localCookies);
  }


}
