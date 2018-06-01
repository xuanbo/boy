package com.xinqing.boy.core.listener;

import com.xinqing.boy.core.Request;
import com.xinqing.boy.core.Spider;

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
     * @param spider 爬虫
     */
    void init(Spider spider);

    /**
     * 请求成功
     *
     * @param spider 爬虫
     * @param request Request
     */
    void onSuccess(Spider spider, Request request);

    /**
     * 请求失败
     *
     * @param spider 爬虫
     * @param request Request
     */
    void onError(Spider spider, Request request);

    /**
     * 完成
     *
     * @param spider 爬虫
     */
    void onComplete(Spider spider);

    /**
     * 销毁
     *
     * @param spider 爬虫
     */
    void destroy(Spider spider);

}
