package com.quick.hui.crawler.core.entity;

/**
 * Created by yuanj on 2017/11/29.
 */
public class LoginAuthTokenData {

  private int code;
  private String result;


  public LoginAuthTokenData(int code, String result) {
    this.code = code;
    this.result = result;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }
}
