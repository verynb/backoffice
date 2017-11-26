package com.quick.hui.crawler.core.test;

import com.google.common.collect.Lists;
import com.quick.hui.crawler.core.entity.CrawlHttpConf.HttpMethod;
import com.quick.hui.crawler.core.entity.CrawlMeta;
import com.quick.hui.crawler.core.entity.CrawlResult;
import com.quick.hui.crawler.core.job.CrawJobResult;
import com.quick.hui.crawler.core.job.SimpleCrawlJob;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class BaseCrawlerTest {


    /**
     *
     */
    @Test
    public void testFetch() throws InterruptedException {
        SimpleCrawlJob job = new SimpleCrawlJob();
        Thread thread = new Thread(job, "crawler-test");
        thread.start();
        thread.join();
    }

}
