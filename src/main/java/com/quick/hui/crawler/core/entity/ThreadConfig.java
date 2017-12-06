package com.quick.hui.crawler.core.entity;

/**
 * Created by yuanj on 2017/12/6.
 */
public class ThreadConfig {

  private Integer mailSpaceTime;
  private Integer requestSpaceTime;
  private Integer mailReceiveErrorTimes;
  private Integer transferErrorTimes;
  private Integer threadspaceTime;
  private Integer threadPoolSize;

  public ThreadConfig(Integer mailSpaceTime, Integer requestSpaceTime, Integer mailReceiveErrorTimes,
      Integer transferErrorTimes, Integer threadspaceTime, Integer threadPoolSize) {
    this.mailSpaceTime = mailSpaceTime;
    this.requestSpaceTime = requestSpaceTime;
    this.mailReceiveErrorTimes = mailReceiveErrorTimes;
    this.transferErrorTimes = transferErrorTimes;
    this.threadspaceTime = threadspaceTime;
    this.threadPoolSize = threadPoolSize;
  }

  public Integer getMailSpaceTime() {
    return mailSpaceTime;
  }

  public void setMailSpaceTime(Integer mailSpaceTime) {
    this.mailSpaceTime = mailSpaceTime;
  }

  public Integer getRequestSpaceTime() {
    return requestSpaceTime;
  }

  public void setRequestSpaceTime(Integer requestSpaceTime) {
    this.requestSpaceTime = requestSpaceTime;
  }

  public Integer getMailReceiveErrorTimes() {
    return mailReceiveErrorTimes;
  }

  public void setMailReceiveErrorTimes(Integer mailReceiveErrorTimes) {
    this.mailReceiveErrorTimes = mailReceiveErrorTimes;
  }

  public Integer getTransferErrorTimes() {
    return transferErrorTimes;
  }

  public void setTransferErrorTimes(Integer transferErrorTimes) {
    this.transferErrorTimes = transferErrorTimes;
  }

  public Integer getThreadspaceTime() {
    return threadspaceTime;
  }

  public void setThreadspaceTime(Integer threadspaceTime) {
    this.threadspaceTime = threadspaceTime;
  }

  public Integer getThreadPoolSize() {
    return threadPoolSize;
  }

  public void setThreadPoolSize(Integer threadPoolSize) {
    this.threadPoolSize = threadPoolSize;
  }
}
