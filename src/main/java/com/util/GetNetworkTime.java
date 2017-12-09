package com.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by yj on 2017/12/9.
 */
public class GetNetworkTime {

  private static final String webUrl = "http://www.taobao.com";//百度

  public static Long getNetworkDatetime() {
    try {
      URL url = new URL(webUrl);// 取得资源对象
      URLConnection uc = url.openConnection();// 生成连接对象
      uc.connect();// 发出连接
      long ld = uc.getDate();// 读取网站日期时间
      return ld;
    } catch (MalformedURLException e) {
      return System.currentTimeMillis();
    } catch (IOException e) {
      return System.currentTimeMillis();
    }
  }
  public static void main(String[]args){
    System.out.print(getNetworkDatetime());
  }
}
