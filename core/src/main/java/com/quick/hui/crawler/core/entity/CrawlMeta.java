package com.quick.hui.crawler.core.entity;


import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by yihui on 2017/6/27.
 */
@ToString
@Data
//@Builder
@NoArgsConstructor
public class CrawlMeta {

  /**
   * 待爬去的网址
   */

  private String url;


  /**
   * 获取指定内容的规则, 因为一个网页中，你可能获取多个不同的内容， 所以放在集合中
   */
  @Setter
  private Set<String> selectorRules;

  public CrawlMeta(String url, Set<String> selectorRules) {
    this.url = url;
    this.selectorRules = selectorRules;
  }


  public Set<String> getSelectorRules() {
    return selectorRules != null ? selectorRules : new HashSet<>();
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setSelectorRules(Set<String> selectorRules) {
    this.selectorRules = selectorRules;
  }
}
