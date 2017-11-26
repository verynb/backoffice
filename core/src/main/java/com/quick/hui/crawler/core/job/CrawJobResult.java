package com.quick.hui.crawler.core.job;

import com.quick.hui.crawler.core.entity.CrawlHttpConf;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.entity.CrawlResult;

/**
 * Created by Administrator on 2017/11/25.
 */
public class CrawJobResult {
  /**
   * 配置项信息
   */
  private CrawlMeta crawlMeta;

  /**
   * http配置信息
   */
  private CrawlHttpConf httpConf = new CrawlHttpConf();

  /**
   * 存储爬取的结果
   */
  private CrawlResult crawlResult = new CrawlResult();

  public CrawlMeta getCrawlMeta() {
    return crawlMeta;
  }

  public void setCrawlMeta(CrawlMeta crawlMeta) {
    this.crawlMeta = crawlMeta;
  }

  public CrawlHttpConf getHttpConf() {
    return httpConf;
  }

  public void setHttpConf(CrawlHttpConf httpConf) {
    this.httpConf = httpConf;
  }

  public CrawlResult getCrawlResult() {
    return crawlResult;
  }

  public void setCrawlResult(CrawlResult crawlResult) {
    this.crawlResult = crawlResult;
  }
}
