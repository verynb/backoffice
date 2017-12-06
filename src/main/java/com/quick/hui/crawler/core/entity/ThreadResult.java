package com.quick.hui.crawler.core.entity;

/**
 * Created by yuanj on 2017/12/6.
 */
public class ThreadResult {

  private int row;
  private Boolean success;

  public ThreadResult(int row, Boolean success) {
    this.row = row;
    this.success = success;
  }

  public int getRow() {
    return row;
  }

  public void setRow(int row) {
    this.row = row;
  }

  public Boolean getSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  @Override
  public String toString() {
    return "ThreadResult{" +
        "row=" + row +
        ", success=" + success +
        '}';
  }
}
