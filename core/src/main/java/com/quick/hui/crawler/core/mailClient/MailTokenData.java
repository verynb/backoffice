package com.quick.hui.crawler.core.mailClient;

import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

/**
 * Created by Administrator on 2017/11/30.
 */
public class MailTokenData {
  private  String token;
  private long date;

  public MailTokenData(String token, long date) {
    this.token = token;
    this.date = date;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public long getDate() {
    return date;
  }

  public void setDate(long date) {
    this.date = date;
  }
}
