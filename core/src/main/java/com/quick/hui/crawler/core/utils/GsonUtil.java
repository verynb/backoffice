package com.quick.hui.crawler.core.utils;

import com.google.gson.Gson;

/**
 * Created by yuanj on 2017/11/28.
 */
public class GsonUtil {

  public static <T> T jsonToObject(String jsonData, Class<T> type) {
    Gson gson = new Gson();
    T result = gson.fromJson(jsonData, type);
    return result;
  }
}
