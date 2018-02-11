package com.quick.hui.crawler.core.entity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

/**
 * Created by yuanj on 2017/12/21.
 */
public class HttpPostResult {

  private HttpClient httpClient;
  private HttpPost httpPost;
  private HttpResponse response;

  public HttpPostResult(HttpClient httpClient, HttpPost httpPost, HttpResponse response) {
    this.httpClient = httpClient;
    this.httpPost = httpPost;
    this.response = response;
  }

  public HttpClient getHttpClient() {
    return httpClient;
  }

  public void setHttpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public HttpPost getHttpPost() {
    return httpPost;
  }

  public void setHttpPost(HttpPost httpPost) {
    this.httpPost = httpPost;
  }

  public HttpResponse getResponse() {
    return response;
  }

  public void setResponse(HttpResponse response) {
    this.response = response;
  }
}
