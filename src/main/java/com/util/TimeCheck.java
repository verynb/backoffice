package com.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by yj on 2017/12/9.
 */
public class TimeCheck {

  private static final Long LIMIT_DAY = 1513526399000L;//天数限制
  private static final Long LIMIT_MONTH=1514735999000L;//月数限制
  public static Boolean checkDay() {
    return GetNetworkTime.getNetworkDatetime()<=LIMIT_DAY;
  }
  public static Boolean checkMonth() {
    return GetNetworkTime.getNetworkDatetime()<=LIMIT_MONTH;
  }
}
