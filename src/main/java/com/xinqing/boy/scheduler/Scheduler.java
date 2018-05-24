package com.xinqing.boy.scheduler;

import com.xinqing.boy.Request;

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
     * @param request Request
     */
    void push(Request request);

    /**
     * 获取一个request
     *
     * @return Request
     */
    Request get();

    /**
     * 总共处理的请求数
     *
     * @return int
     */
    int getTotal();

    /**
     * 待处理的请求数
     *
     * @return int
     */
    int getLeft();

}
