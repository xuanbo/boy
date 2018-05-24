package com.xinqing.boy.listener;

import com.xinqing.boy.Request;

/**
 * 爬虫监听
 *
 * @author xuan
 * @since 1.0.0
 */
public interface SpiderListener {

    /**
     * 初始化
     *
     * @param name 爬虫名
     */
    void init(String name);

    /**
     * 请求成功
     *
     * @param request Request
     */
    void onSuccess(Request request);

    /**
     * 请求失败
     *
     * @param request Request
     */
    void onError(Request request);

    /**
     * 完成
     */
    void onComplete();

    /**
     * 销毁
     */
    void destroy();

}
