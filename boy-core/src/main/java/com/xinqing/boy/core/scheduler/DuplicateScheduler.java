package com.xinqing.boy.core.scheduler;

import com.xinqing.boy.core.Request;
import com.xinqing.boy.core.Spider;
import com.xinqing.boy.core.util.UrlUtil;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 去除重复调度器
 *
 * @author xuan
 * @since 1.0.0
 */
public abstract class DuplicateScheduler implements Scheduler {

    /**
     * Set去除重复url
     */
    private Set<String> urls = new ConcurrentSkipListSet<>();

    @Override
    public void push(Spider spider, Request request) {
        // 不是重试或者重复请求，则放入
        if (isRetryRequest(spider, request) || !isDuplicate(spider, request)) {
            pushWhenNoDuplicate(spider, request);
        }
    }

    @Override
    public int getTotal(Spider spider) {
        return urls.size();
    }

    /**
     * 请求是否重复
     *
     * @param spider Spider
     * @param request Request
     * @return boolean
     */
    protected boolean isDuplicate(Spider spider, Request request) {
        return !urls.add(UrlUtil.getPath(request.getUrl()));
    }

    /**
     * 请求不重复则放入
     *
     * @param spider 爬虫
     * @param request Request
     */
    protected abstract void pushWhenNoDuplicate(Spider spider, Request request);

    /**
     * 判断请求是否重试
     *
     * @param spider 爬虫
     * @param request Request
     * @return boolean
     */
    private boolean isRetryRequest(Spider spider, Request request) {
        return request.getRetryTime() > 0;
    }

}
