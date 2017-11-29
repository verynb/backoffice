package com.quick.hui.crawler.core.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/11/27.
 */
public class TransferPageData {

  private String authToken;
  private String transferUserId;
  private List<TransferWallet> transferWallets;

  public TransferPageData(String authToken, String transferUserId, List<TransferWallet> transferWallets) {
    this.authToken = authToken;
    this.transferUserId = transferUserId;
    this.transferWallets = transferWallets;
  }

  public String getAuthToken() {
    return authToken;
  }

  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }

  public String getTransferUserId() {
    return transferUserId;
  }

  public void setTransferUserId(String transferUserId) {
    this.transferUserId = transferUserId;
  }

  public List<TransferWallet> getTransferWallets() {
    return transferWallets;
  }

  public void setTransferWallets(
      List<TransferWallet> transferWallets) {
    this.transferWallets = transferWallets;
  }
}
