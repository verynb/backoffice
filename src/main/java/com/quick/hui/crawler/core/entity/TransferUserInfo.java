package com.quick.hui.crawler.core.entity;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by yuanj on 2017/12/1.
 */

public class TransferUserInfo {

  private int row;
  private String userName;
  private String password;
  private String email;
  private String mailPassword;
  private String transferTo;

  public TransferUserInfo(int row, String userName, String password, String email, String mailPassword,
      String transferTo) {
    this.row = row;
    this.userName = userName;
    this.password = password;
    this.email = email;
    this.mailPassword = mailPassword;
    this.transferTo = transferTo;
  }

  public TransferUserInfo() {
  }

  public int getRow() {
    return row;
  }

  public void setRow(int row) {
    this.row = row;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getMailPassword() {
    return mailPassword;
  }

  public void setMailPassword(String mailPassword) {
    this.mailPassword = mailPassword;
  }

  public String getTransferTo() {
    return transferTo;
  }

  public void setTransferTo(String transferTo) {
    this.transferTo = transferTo;
  }


  public Boolean filterUserInfo() {
    Boolean status = true;
    if (StringUtils.isBlank(this.getUserName())) {
      status = false;
    }
    if (StringUtils.isBlank(this.getPassword())) {
      status = false;
    }
    if (StringUtils.isBlank(this.getEmail())) {
      status = false;
    }
    if (StringUtils.isBlank(this.getMailPassword())) {
      status = false;
    }
    if (StringUtils.isBlank(this.getTransferTo())) {
      status = false;
    }
    return status;
  }


  @Override
  public String toString() {
    return "TransferUserInfo{" +
        "转出帐号='" + userName + '\'' +
        ", 转出帐号密码='" + password + '\'' +
        ", 转出帐号邮箱='" + email + '\'' +
        ", 邮箱密码='" + mailPassword + '\'' +
        ", 接收帐号='" + transferTo + '\'' +
        '}';
  }
}
