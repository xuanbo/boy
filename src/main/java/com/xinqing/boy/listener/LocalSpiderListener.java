package com.xinqing.boy.listener;

import com.xinqing.boy.Request;
import com.xinqing.boy.Spider;
import com.xinqing.boy.scheduler.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 本地爬虫监听器
 *
 * @author xuan
 * @since 1.0.0
 */
public class LocalSpiderListener implements SpiderListener {

    private static final Logger LOG = LoggerFactory.getLogger(LocalSpiderListener.class);

    /**
     * 调度器
     */
    private final Scheduler scheduler;

    /**
     * 统计成功次数
     */
    private AtomicInteger successCounter;

    /**
     * 统计失败次数
     */
    private AtomicInteger errorCounter;

    public LocalSpiderListener(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void init(Spider spider) {
        successCounter = new AtomicInteger();
        errorCounter = new AtomicInteger();
    }

    @Override
    public void onSuccess(Spider spider, Request request) {
        successCounter.incrementAndGet();
    }

    @Override
    public void onError(Spider spider, Request request) {
        errorCounter.incrementAndGet();
    }

    @Override
    public void onComplete(Spider spider) {
        LOG.info("spider[{}] report: total({}), success({}), error({})", spider.getName() + "@" + spider.getId(), scheduler.getTotal(spider), successCounter.get(), errorCounter.get());
    }

    @Override
    public void destroy(Spider spider) {
        successCounter = null;
        errorCounter = null;
    }

}
