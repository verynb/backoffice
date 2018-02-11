package com.quick.hui.crawler.core.entity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

/**
 * Created by yuanj on 2017/12/21.
 */
public class HttpResult {

  private HttpClient httpClient;
  private HttpGet httpGet;
  private HttpResponse response;

  public HttpResult(HttpClient httpClient, HttpGet httpGet, HttpResponse response) {
    this.httpClient = httpClient;
    this.httpGet = httpGet;
    this.response = response;
  }

  public HttpClient getHttpClient() {
    return httpClient;
  }

  public void setHttpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public HttpGet getHttpGet() {
    return httpGet;
  }

  public void setHttpGet(HttpGet httpGet) {
    this.httpGet = httpGet;
  }

  public HttpResponse getResponse() {
    return response;
  }

  public void setResponse(HttpResponse response) {
    this.response = response;
  }
}
