package com.quick.hui.crawler.core.loadUserData;

import com.google.common.collect.Lists;
import com.quick.hui.crawler.core.entity.TransferUserInfo;
import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoadData {

  public static List<TransferUserInfo> loadUserInfoData(String filePath) {
    CsvReader csvReader = new CsvReader();
    csvReader.setContainsHeader(true);
    CsvContainer csv = null;
    List<TransferUserInfo> userInfos = Lists.newArrayList();
    try {
      csv = csvReader.read(Paths.get(filePath), StandardCharsets.UTF_8);
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
    }
    return userInfos;
  }
}
