package com.quick.hui.crawler.core.entity;

/**
 * Created by Administrator on 2017/11/27.
 */
public class TransferWallet {

  private String walletId;
  private Double amount;

  public TransferWallet(String walletId, Double amount) {
    this.walletId = walletId;
    this.amount = amount;
  }

  public String getWalletId() {
    return walletId;
  }

  public void setWalletId(String walletId) {
    this.walletId = walletId;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }
}
