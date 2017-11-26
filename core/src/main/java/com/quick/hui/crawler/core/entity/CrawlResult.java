package com.quick.hui.crawler.core.entity;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jsoup.nodes.Document;

/**
 * Created by yihui on 2017/6/27.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class CrawlResult {


    public static Status SUCCESS = new Status(200, "success");
    public static Status NOT_FOUND = new Status(494, "not found");


    private Status status;


    /**
     * 爬取的网址
     */
    private String url;


    /**
     * 爬取的网址对应的 DOC 结构
     */
    private Document htmlDoc;


    /**
     * 选择的结果，key为选择规则，value为根据规则匹配的结果
     */
    private Map<String, List<String>> result= Maps.newHashMap();



    public void setStatus(int code, String msg) {
        this.status = new Status(code, msg);
    }

    public static Status getSUCCESS() {
        return SUCCESS;
    }

    public static void setSUCCESS(Status SUCCESS) {
        CrawlResult.SUCCESS = SUCCESS;
    }

    public static Status getNotFound() {
        return NOT_FOUND;
    }

    public static void setNotFound(Status notFound) {
        NOT_FOUND = notFound;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Document getHtmlDoc() {
        return htmlDoc;
    }

    public void setHtmlDoc(Document htmlDoc) {
        this.htmlDoc = htmlDoc;
    }

    public Map<String, List<String>> getResult() {
        return result;
    }

    public void setResult(Map<String, List<String>> result) {
        this.result = result;
    }
}
