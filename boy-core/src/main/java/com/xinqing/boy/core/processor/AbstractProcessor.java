package com.xinqing.boy.core.processor;

import com.xinqing.boy.core.Item;
import com.xinqing.boy.core.Response;
import com.xinqing.boy.core.Result;

import java.util.List;
import java.util.Map;

/**
 * @author xuan
 * @since 1.0.0
 */
public abstract class AbstractProcessor implements Processor {

    private Result result;

    @Override
    public Result parse(Response response) {
        result = response.createResult();
        doParse(response);
        return result;
    }

    /**
     * 添加url
     *
     * @param url 请求url
     */
    protected void next(String url) {
        next(url, null, null);
    }

    /**
     * 添加url并设置回调
     *
     * @param url 请求url
     * @param callback 回调
     */
    protected void next(String url, Processor callback) {
        next(url, callback, null);
    }

    /**
     * 添加url并设置回调以及参数
     *
     * @param url 请求url
     * @param callback 回调
     * @param props 回调参数
     */
    protected void next(String url, Processor callback, Map<String, Object> props) {
        result.addNextUrl(url);
        result.setCallback(callback);
        result.setProps(props);
    }

    /**
     * 添加urls
     *
     * @param urls 请求urls
     */
    protected void next(List<String> urls) {
        next(urls, null, null);
    }

    /**
     * 添加urls并设置回调
     *
     * @param urls 请求urls
     * @param callback 回调
     */
    protected void next(List<String> urls, Processor callback) {
        next(urls, callback, null);
    }

    /**
     * 添加urls并设置回调以及参数
     *
     * @param urls 请求urls
     * @param callback 回调
     * @param props 回调参数
     */
    protected void next(List<String> urls, Processor callback, Map<String, Object> props) {
        urls.forEach(result::addNextUrl);
        result.setCallback(callback);
        result.setProps(props);
    }

    /**
     * 设置urls
     *
     * @param urls 请求urls
     */
    protected void setNext(List<String> urls) {
        setNext(urls, null, null);
    }

    /**
     * 设置urls并设置回调
     *
     * @param urls 请求urls
     * @param callback 回调
     */
    protected void setNext(List<String> urls, Processor callback) {
        setNext(urls, callback, null);
    }

    /**
     * 设置urls并设置回调以及参数
     *
     * @param urls 请求urls
     * @param callback 回调
     * @param props 回调参数
     */
    protected void setNext(List<String> urls, Processor callback, Map<String, Object> props) {
        result.setNextUrls(urls);
        result.setCallback(callback);
        result.setProps(props);
    }

    /**
     * 设置回调
     *
     * @param callback url回调
     */
    protected void callback(Processor callback) {
        result.setCallback(callback);
    }

    /**
     * 设置回调参数
     *
     * @param props 回调参数
     */
    protected void props(Map<String, Object> props) {
        result.setProps(props);
    }

    /**
     * 设置Item
     *
     * @param item Item
     */
    protected void setItem(Item item) {
        result.setItem(item);
    }

    /**
     * 解析
     *
     * @param response Response
     */
    protected abstract void doParse(Response response);

}
