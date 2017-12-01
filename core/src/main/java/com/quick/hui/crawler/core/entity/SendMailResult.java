package com.quick.hui.crawler.core.entity;

/**
 * Created by yuanj on 2017/11/28.
 */

public class SendMailResult {
  private String status;
  private String error;

  public SendMailResult(String status, String error) {
    this.status = status;
    this.error = error;
  }
  public SendMailResult(){}

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
