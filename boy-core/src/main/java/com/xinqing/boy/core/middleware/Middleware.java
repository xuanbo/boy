package com.xinqing.boy.core.middleware;

import com.xinqing.boy.core.Request;

/**
 * 中间件
 *
 * @author xuan
 * @since 1.0.0
 */
public interface Middleware {

    /**
     * 处理request
     *
     * @param request Request
     */
    void process(Request request);

    /**
     * 排序，值越小优先级越高
     *
     * @return int
     */
    int order();

}
