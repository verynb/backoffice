package com.quick.hui.crawler.core.ocr;

import com.baidu.aip.ocr.AipOcr;
import java.util.HashMap;
import org.json.JSONObject;

/**
 * Created by yuanj on 2017/11/28.
 */
public class OcrDemo {

  //设置APPID/AK/SK
  public static final String APP_ID = "10451608";
  public static final String API_KEY = "B8RGxxS6SaIOyGSB6ryNUAAK";
  public static final String SECRET_KEY = "vMWz5fXbZb0oblGdqp2YvSgplgy1dB7o";

  public void generalRecognition(AipOcr client) {
    // 自定义参数定义
    HashMap<String, String> options = new HashMap<String, String>();
    options.put("detect_direction", "true");
    options.put("language_type", "ENG");

    // 参数为图片url
//    String url = "https://pin2.aliyun.com/get_img?type=150_40&identity=mailsso.mxhichina.com&sessionid=k0JThIj0sb3Tw088n7Ft0Lv/QvqESTXvkqtKbrJ9Z9Uazrb4V5v+oFyQ==";

    String imagePath = "D:\\backoffice\\get_img.jpg";
//    byte[] file = readImageFile(imagePath);
    JSONObject response3 = client.accurateGeneral(imagePath, options);
    System.out.println(response3.toString());


    /*String imagePath = "general.jpg";
    JSONObject response = client.accurateGeneral(imagePath);
    System.out.println(response.toString());*/
  }

  public static void main(String[] args) {
    // 初始化一个AipOcr
    AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

    // 可选：设置网络连接参数
    client.setConnectionTimeoutInMillis(2000);
    client.setSocketTimeoutInMillis(60000);

    // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
    //client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
   // client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

    // 调用接口
    new OcrDemo().generalRecognition(client);

  }

}
