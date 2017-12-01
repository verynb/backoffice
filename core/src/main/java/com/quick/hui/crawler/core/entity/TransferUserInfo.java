package com.quick.hui.crawler.core.entity;

/**
 * Created by yuanj on 2017/12/1.
 */

public class TransferUserInfo {

  private String userName;
  private String password;
  private String email;
  private String mailPassword;
  private String transferTo;

  public TransferUserInfo(String userName, String password, String email, String mailPassword,
      String transferTo) {
    this.userName = userName;
    this.password = password;
    this.email = email;
    this.mailPassword = mailPassword;
    this.transferTo = transferTo;
  }

  public TransferUserInfo() {
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
}
