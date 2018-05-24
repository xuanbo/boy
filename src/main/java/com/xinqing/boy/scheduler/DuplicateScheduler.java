package com.xinqing.boy.scheduler;

import com.xinqing.boy.Request;
import com.xinqing.boy.util.UrlUtil;

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

    private boolean isDuplicate(Request request) {
        return !urls.add(UrlUtil.getPath(request.getUrl()));
    }

    @Override
    public void push(Request request) {
        // 不是重试或者重复请求，则放入
        if (isRetryRequest(request) || !isDuplicate(request)) {
            pushWhenNoDuplicate(request);
        }
    }

    @Override
    public int getTotal() {
        return urls.size();
    }

    /**
     * 请求不重复则放入
     *
     * @param request Request
     */
    protected abstract void pushWhenNoDuplicate(Request request);

    /**
     * 判断请求是否重试
     *
     * @param request Request
     * @return boolean
     */
    private boolean isRetryRequest(Request request) {
        return request.getRetryTime() > 0;
    }

}
