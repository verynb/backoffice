package com.quick.hui.crawler.core.job;

/**
 * Created by yihui on 2017/6/27.
 */
public abstract class AbstractJob implements IJob {

    public void beforeRun() {
    }

    public void afterRun() {
    }


    @Override
    public void run() {
        try {
            this.beforeRun();
            Thread.sleep(2000);
            this.doFetchPage();
        } catch (Exception e) {
        }
        this.afterRun();
    }


    /**
     * 具体的抓去网页的方法， 需要子类来补全实现逻辑
     *
     * @throws Exception
     */
    public abstract void doFetchPage() throws Exception;
}
