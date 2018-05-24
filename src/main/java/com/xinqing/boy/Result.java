package com.xinqing.boy;

import com.xinqing.boy.util.UrlUtil;

import java.util.ArrayList;
import java.util.List;
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

}
