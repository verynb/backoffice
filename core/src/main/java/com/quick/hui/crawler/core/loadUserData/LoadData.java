package com.quick.hui.crawler.core.loadUserData;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.quick.hui.crawler.core.entity.TransferUserInfo;
import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class LoadData {
  private static Logger logger = LoggerFactory.getLogger(LoadData.class);
  public static List<TransferUserInfo> loadUserInfoData(String filePath) {
    CsvReader csvReader = new CsvReader();
    csvReader.setContainsHeader(true);
    CsvContainer csv = null;
    List<TransferUserInfo> userInfos = Lists.newArrayList();
    try {
      csv = csvReader.read(Paths.get(filePath), StandardCharsets.UTF_8);
      if(csv==null){
      }
      for (CsvRow row : csv.getRows()) {
        TransferUserInfo userInfo = new TransferUserInfo(
            row.getField("tuser"),
            row.getField("tpassword"),
            row.getField("tmail"),
            row.getField("tmailpassword"),
            row.getField("ruser"));
        if (userInfo.filterUserInfo()) {
          userInfos.add(userInfo);
        }
      }
    } catch (IOException e) {
      logger.info("加载用户数据失败"+e.getMessage());
      throw new RuntimeException();
    }
    logger.info("加载用户数据成功");
    return userInfos;
  }


  public static Map loadCookies(String filePath) {
    CsvReader csvReader = new CsvReader();
    csvReader.setContainsHeader(true);
    CsvContainer csv = null;
    Map<String,String> cookieMap = Maps.newHashMap();
    try {
      csv = csvReader.read(Paths.get(filePath), StandardCharsets.UTF_8);
      if(csv==null){
        logger.info("加载cookie数据失败");
        throw new RuntimeException();
      }
      for (CsvRow row : csv.getRows()) {
        cookieMap.put(row.getField("key"), row.getField("value"));
      }
    } catch (IOException e) {
      logger.error("加载cookie数据失败"+e.getMessage());
    }
    logger.info("加载cookie数据成功");
    return cookieMap;
  }
}
