package com.util;

import java.util.Random;

/**
 * Created by yuanj on 2017/12/6.
 */
public class RandomUtil {

  public static int ranNum(int rand) {
    Random rands = new Random();
    return rands.nextInt(rand);
  }

}
