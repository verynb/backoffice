package com.quick.hui.crawler.core.entity;

/**
 * Created by Administrator on 2017/11/26.
 */
public class Status {
  private int code;

  private String msg;

  public Status(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}
