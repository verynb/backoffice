package com.quick.hui.crawler.core.entity;

/**
 * Created by yuanj on 2017/11/28.
 */

public class TransferResult {
  private String status;
  private String error;

  public TransferResult(String status, String error) {
    this.status = status;
    this.error = error;
  }
  public TransferResult(){}

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

  @Override
  public String toString() {
    return "TransferResult{" +
        "status='" + status + '\'' +
        ", error='" + error + '\'' +
        '}';
  }
}
