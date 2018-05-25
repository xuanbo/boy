package com.xinqing.boy;

import com.xinqing.boy.processor.Processor;
import com.xinqing.boy.util.CollectionUtil;
import com.xinqing.boy.util.UrlUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 结果
 *
 * @author xuan
 * @since 1.0.0
 */
public class Result {

    /**
     * 当前响应的url
     */
    private String url;

    /**
     * 下次请求url
     */
    private List<String> nextUrls = new ArrayList<>();

    /**
     * 下次请求url回调
     */
    private Processor callback;

    /**
     * 回调传递参数
     */
    private Map<String, Object> props;

    /**
     * 数据
     */
    private Item item;

    private Result(String url) {
        this.url = url;
    }

    public static Result of(Response response) {
        return new Result(response.getUrl());
    }

    public List<String> getNextUrls() {
        return nextUrls;
    }

    public Result addNextUrl(String nextUrl) {
        this.nextUrls.add(UrlUtil.getAbsoluteUrl(url, nextUrl));
        return this;
    }

    public Result setNextUrls(List<String> nextUrls) {
        this.nextUrls = nextUrls.stream().map(nextUrl -> UrlUtil.getAbsoluteUrl(url, nextUrl)).collect(Collectors.toList());
        return this;
    }

    public Item getItem() {
        return item;
    }

    public Result setItem(Item item) {
        this.item = item;
        return this;
    }

    public Processor getCallback() {
        return callback;
    }

    public Result setCallback(Processor callback) {
        this.callback = callback;
        return this;
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public Result setProps(Map<String, Object> props) {
        this.props = props;
        return this;
    }

    public List<Request> toRequests() {
        if (CollectionUtil.isEmpty(nextUrls)) {
            return Collections.emptyList();
        }
        return nextUrls.stream().map(this::toRequest).collect(Collectors.toList());
    }

    private Request toRequest(String nextUrl) {
        Request request = new Request();
        request.setUrl(nextUrl);
        request.setCallback(callback);
        request.setProps(props);
        return request;
    }
}
