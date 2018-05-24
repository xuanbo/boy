package com.xinqing.boy.processor;

import com.xinqing.boy.Item;
import com.xinqing.boy.Response;
import com.xinqing.boy.Result;

import java.util.List;

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
    protected void nextUrl(String url) {
        result.addNextUrl(url);
    }

    /**
     * 添加urls
     *
     * @param urls 请求urls
     */
    protected void nextUrls(List<String> urls) {
        urls.forEach(result::addNextUrl);
    }

    /**
     * 设置urls
     *
     * @param urls 请求urls
     */
    protected void setNextUrls(List<String> urls) {
        result.setNextUrls(urls);
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
