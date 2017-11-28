package com.quick.hui.crawler.core.entity;

/**
 * Created by yuanj on 2017/11/28.
 */

public class SendMailResult {
  private String status;
  private String error;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }
}
