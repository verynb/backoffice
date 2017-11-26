package com.quick.hui.crawler.core.utils;

import com.google.common.collect.Lists;
import com.quick.hui.crawler.core.entity.CrawlHttpConf;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.localSession.LocalCookie;
import com.quick.hui.crawler.core.localSession.Session;
import java.util.stream.Collectors;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yihui on 2017/6/27.
 */
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


  private static HttpResponse doGet(CrawlMeta crawlMeta, CrawlHttpConf httpConf) throws Exception {
//        HttpClient httpClient = HttpClients.createDefault();
    SSLContextBuilder builder = new SSLContextBuilder();
//         全部信任 不做身份鉴定
    builder.loadTrustMaterial(null, (x509Certificates, s) -> true);
//        HttpClient httpClient = HttpClientBuilder.create().setSslcontext(builder.build()).build();
    CookieStore cookieStore = new BasicCookieStore();
    HttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
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
//    httpGet.addHeader("cookie","visid_incap_796901=xwCteSVhTiev8CG0pbGoAp4fGVoAAAAAQUIPAAAAAAA+TpKDS6EuPtUQVxZh1nvb; __guid=40576676.4593505855663582700.1511596720205.5698; nlbi_796901=5/sVeqpIuBTOSS/iLMejiQAAAAAXwKLFfJL5LTFfPmiPBw7L; incap_ses_873_796901=ianZCrDNXV/VIPfs9IQdDNUfGVoAAAAA2YXAbWDUBLschuVvA6Hy3g==; _bidsbackoffice_sessions=emdPLzdVa2k0Ni8weFl6QnhXdXdDd3hvbmRUYXFncDFmdXpxUitibHEzVkdJc2xSVThlRFA2Y2pPYUZTVEFYVFlTbnpPQnJ5ZVpKbW5qZUJZejNxSWVxKzlsNk56WEFBam5tNVhnUE56aytyVndQQi9KbEo0aVpEa3BvalFDRnl1eTA1ZTJvQ3ZhQ2J4dHJoWHhOYytBPT0tLVVEZ0V1b1hjNWIzOUhFek9FWVl0bHc9PQ%3D%3D--9e62bade711229a31188803f028ef1f1026d62e6; monitor_count=4; incap_ses_128_796901=xgOeLS0feQ7qA/6HS8DGATArGVoAAAAAUQZWaWVcnT16R829srRq+g==");
    // 执行网络请求
    HttpResponse response = httpClient.execute(httpGet);
    List<Cookie> cookies = cookieStore.getCookies();
//    List<Cookie> cookies = Lists.newArrayList();
    List<LocalCookie> localCookies = cookies.stream().map(c -> {
      LocalCookie localCookie = new LocalCookie();
      localCookie.setSessionKey(c.getName());
      localCookie.setSessionValue(c.getValue());
      return localCookie;
    }).collect(Collectors.toList());
    Session.writeSession(localCookies);
    return response;
  }


  private static HttpResponse doPost(CrawlMeta crawlMeta, CrawlHttpConf httpConf) throws Exception {
//        HttpClient httpClient = HttpClients.createDefault();
    SSLContextBuilder builder = new SSLContextBuilder();
//         全部信任 不做身份鉴定
    builder.loadTrustMaterial(null, (x509Certificates, s) -> true);
//    HttpClient httpClient = HttpClientBuilder.create().setSslcontext(builder.build()).build();
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
    List<Cookie> cookies = cookieStore.getCookies();
    List<LocalCookie> localCookies = cookies.stream().map(c -> {
      LocalCookie localCookie = new LocalCookie();
      localCookie.setSessionKey(c.getName());
      localCookie.setSessionValue(c.getValue());
      return localCookie;
    }).collect(Collectors.toList());
    Session.writeSession(localCookies);
    return response;
  }


}
