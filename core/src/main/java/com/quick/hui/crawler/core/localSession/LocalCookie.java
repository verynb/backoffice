package com.quick.hui.crawler.core.localSession;

/**
 * Created by yj on 2017/11/25.
 */
public class LocalCookie {

  private String sessionKey;

  private String sessionValue;

  public LocalCookie(){}
  public LocalCookie(String sessionKey, String sessionValue) {
    this.sessionKey = sessionKey;
    this.sessionValue = sessionValue;
  }

  public String getSessionKey() {
    return sessionKey;
  }

  public void setSessionKey(String sessionKey) {
    this.sessionKey = sessionKey;
  }

  public String getSessionValue() {
    return sessionValue;
  }

  public void setSessionValue(String sessionValue) {
    this.sessionValue = sessionValue;
  }
}
