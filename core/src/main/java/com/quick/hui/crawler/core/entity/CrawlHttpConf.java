package com.quick.hui.crawler.core.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.quick.hui.crawler.core.localSession.LocalCookie;
import com.quick.hui.crawler.core.localSession.Session;
import java.util.List;
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
    DEFAULT_HEADERS.put("connection", "Keep-Alive");
    DEFAULT_HEADERS.put("user-agent",
        "Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
    DEFAULT_HEADERS.put("accept-encoding", "gzip,deflate,sdch,br");
    DEFAULT_HEADERS.put("accept-language", "zh-CN,zh;q=0.8");
    DEFAULT_HEADERS.put("cache-control", "max-age=0");
    DEFAULT_HEADERS.put("if-none-match", "W/\"d3e8610f2f2280039cab856b36c50467\"");
    DEFAULT_HEADERS.put("upgrade-insecure-requests", "1");

  }

  public CrawlHttpConf() {
  }

  public CrawlHttpConf(HttpMethod httpMethod) {
    this.method = httpMethod;
  }

  public CrawlHttpConf buildCookie(String cookies) {
    if (Session.get() == null || CollectionUtils.isEmpty(Session.getCookies())) {
      List<LocalCookie> localCookies = Lists.newArrayList(new LocalCookie("visid_incap_796901",
              "azbNT7SxSkKa4sjC0S40DCegGloAAAAAQUIPAAAAAADWPP+CXlYxBIeBB1AVrj8B"),
          new LocalCookie("_bidsbackoffice_sessions", "bHZjUVBBK0VVTjhvNWNkWDNuTEJ3NGZLRFFYT3hTZ0Y2ZjNVQWRZUmlwUVZRWFc1VGZCR3NkczJiZ2paYXArbWZOTG5mM0FzOGdLZDdIci9uanFGUmZMMmpJVXlacFA1ZTY4S01nejlOZWFBWVNqK0JUUllReFpKZzQxNFpoMjBzd09zZWg3cGNSdWFzMHU5Q1NCeVJQaUVwUEtwMUhQb1dRWXJGQzcyODdPdXJ4Q1JxQ3Ftbm03TmFUOE9XQkdiZHNWam11SFUyWFVhVlhHdkdpbEZQRVZPSnJOaE9zcHhVWFZ6YnBHY1d4d3FRU3ROMG41emExaXBsNHBoMHhHc1VpNnM0RmlsYnVlUXlYeXVENHFlVTZTVzBWZkRFK0MxUEs3K1kxNFNOMmlxanNEcmdWZzRUN0RjZThtSTlKa3lITHhaTDlhTW5Edy82bHNOTGxOb3hraWJTcGhjTnFBOWZzckVHNFJqUVlVVzZaVTUrc21TR09tRmFZdVVCTjJYb0kzTU1KMTg5cWt0OGVrTHdIUU1aYWVaUW1jMWRScHEwWFpnUDFVTThZRDZGRk5lTjB6VFlTQitUWHNiK3VWYi0tTXRQWUZsNlFlQ2NET25QeXBHcS9IUT09--9a5f50257015c4c44bce32d3bca45fb2248a857e"),
          new LocalCookie("nlbi_796901",
              "xctkCdRXgAa6RyOoLMejiQAAAAAv2Gcb213eCynCkQPD5EQe"),
          new LocalCookie("incap_ses_533_796901",
              "HqbkMWnu5h9/+Y9DA5llB1YrHFoAAAAAKS1ULVmesIZEFsdR4tu8+w=="));
      Session session = Session.buildSession(localCookies);
      Session.persistenceCurrentSession(session);
//      DEFAULT_HEADERS.put("cookie",cookies);
    }
      List<String> cookieStrings = Session.getCookies().stream()
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
      DEFAULT_HEADERS.put("cookie", buffer.toString());
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
  private Map<String, String> requestHeaders;


  /**
   * 请求参数
   */
  @Setter
  private Map<String, Object> requestParams= Maps.newHashMap();


  public Map<String, String> getRequestHeaders() {
    return DEFAULT_HEADERS;
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
