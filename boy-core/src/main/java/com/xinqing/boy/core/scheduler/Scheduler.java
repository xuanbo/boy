package com.xinqing.boy.core.scheduler;

import com.xinqing.boy.core.Request;
import com.xinqing.boy.core.Spider;

/**
 * 调度器
 *
 * @author xuan
 * @since 1.0.0
 */
public interface Scheduler {

    /**
     * 放入request
     *
     * @param spider 爬虫
     * @param request Request
     */
    void push(Spider spider, Request request);

    /**
     * 获取一个request
     *
     * @param spider 爬虫
     * @return Request
     */
    Request get(Spider spider);

    /**
     * 总共处理的请求数
     *
     * @param spider 爬虫
     * @return int
     */
    int getTotal(Spider spider);

    /**
     * 待处理的请求数
     *
     * @param spider 爬虫
     * @return int
     */
    int getLeft(Spider spider);

}
