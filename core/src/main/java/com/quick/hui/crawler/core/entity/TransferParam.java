package com.quick.hui.crawler.core.entity;

/**
 * Created by yuanj on 2017/11/29.
 */

public class TransferParam {

  private String authenticityToken;
  private String transferTo;
  private String userWalletId;
  private Double amount;
  private String token;
  private String userId;
  private String receiverId;
//  private String authenticityToken;

  public TransferParam(String authenticityToken, String transferTo, String userWalletId, Double amount,
      String token, String userId, String receiverId) {
    this.authenticityToken = authenticityToken;
    this.transferTo = transferTo;
    this.userWalletId = userWalletId;
    this.amount = amount;
    this.token = token;
    this.userId = userId;
    this.receiverId = receiverId;
  }

  public String getAuthenticityToken() {
    return authenticityToken;
  }

  public void setAuthenticityToken(String authenticityToken) {
    this.authenticityToken = authenticityToken;
  }

  public String getTransferTo() {
    return transferTo;
  }

  public void setTransferTo(String transferTo) {
    this.transferTo = transferTo;
  }

  public String getUserWalletId() {
    return userWalletId;
  }

  public void setUserWalletId(String userWalletId) {
    this.userWalletId = userWalletId;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(String receiverId) {
    this.receiverId = receiverId;
  }
}
