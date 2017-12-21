package com.quick.hui.crawler.core.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.quick.hui.crawler.core.localSession.LocalCookie;
import com.quick.hui.crawler.core.localSession.Session;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;

/**
 * http的相关配置
 *
 * 1. 请求参数头
 * 2. 返回的各项设置
 *
 * Created by yihui on 2017/6/27.
 */
@ToString
public class CrawlHttpConf {

  private static Map<String, String> DEFAULT_HEADERS;

  static {
    DEFAULT_HEADERS = new HashMap<>();
    DEFAULT_HEADERS.put("accept",
        "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
    DEFAULT_HEADERS.put("Connection", "close");
    DEFAULT_HEADERS.put("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
    DEFAULT_HEADERS.put("accept-encoding", "gzip,deflate");
    DEFAULT_HEADERS.put("accept-language", "zh-CN,zh;q=0.8");
    DEFAULT_HEADERS.put("cache-control", "max-age=0");
//    DEFAULT_HEADERS.put("if-none-match", "W/\"d3e8610f2f2280039cab856b36c50467\"");
    DEFAULT_HEADERS.put("upgrade-insecure-requests", "1");

  }

  public CrawlHttpConf() {
  }

  public CrawlHttpConf(HttpMethod httpMethod) {
    this.method = httpMethod;
  }

  public CrawlHttpConf buildCookie() {
    if(Session.get()!=null && CollectionUtils.isNotEmpty(Session.getCookies())){
      List<String> cookieStrings = Session
          .getCookies()
          .stream()
          .map(c -> {
            return c.getSessionKey() + "=" + c.getSessionValue();
          }).collect(Collectors.toList());
      StringBuffer buffer = new StringBuffer();

      for (int i = 0; i < cookieStrings.size(); i++) {
        buffer.append(cookieStrings.get(i));
        if (i < cookieStrings.size() - 1) {
          buffer.append("; ");
        }
      }
      requestHeaders.put("cookie", buffer.toString());
    }
    return this;
  }

  public enum HttpMethod {
    GET,
    POST,
    OPTIONS,
    PUT;
  }


  @Getter
  @Setter
  private HttpMethod method = HttpMethod.GET;


  /**
   * 请求头
   */
  @Setter
  private Map<String, String> requestHeaders = Maps.newHashMap();


  /**
   * 请求参数
   */
  @Setter
  private Map<String, Object> requestParams = Maps.newHashMap();


  public Map<String, String> getRequestHeaders() {
    this.requestHeaders.putAll(DEFAULT_HEADERS);
    return requestHeaders;
  }

  public Map<String, Object> getRequestParams() {
    return requestParams == null ? Collections.emptyMap() : requestParams;
  }

  public static Map<String, String> getDefaultHeaders() {
    return DEFAULT_HEADERS;
  }

  public static void setDefaultHeaders(Map<String, String> defaultHeaders) {
    DEFAULT_HEADERS = defaultHeaders;
  }

  public HttpMethod getMethod() {
    return method;
  }

  public void setMethod(HttpMethod method) {
    this.method = method;
  }

  public void setRequestHeaders(Map<String, String> requestHeaders) {
    this.requestHeaders = requestHeaders;
  }

  public void setRequestParams(Map<String, Object> requestParams) {
    this.requestParams = requestParams;
  }
}
