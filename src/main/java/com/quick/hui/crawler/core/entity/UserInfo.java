package com.quick.hui.crawler.core.entity;

/**
 * Created by yuanj on 2017/11/28.
 */

public class  UserInfo {
  private String user_id;
  private String name;
  private Boolean response;

  public String getUser_id() {
    return user_id;
  }

  public void setUser_id(String user_id) {
    this.user_id = user_id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getResponse() {
    return response;
  }

  public void setResponse(Boolean response) {
    this.response = response;
  }

  @Override
  public String toString() {
    return "UserInfo{" +
        "user_id='" + user_id + '\'' +
        ", name='" + name + '\'' +
        ", response=" + response +
        '}';
  }
}
