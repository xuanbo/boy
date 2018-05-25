package com.xinqing.boy;

import com.xinqing.boy.processor.Processor;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求
 *
 * @author xuan
 * @since 1.0.0
 */
public class Request {

    /**
     * `GET`请求
     */
    public static final String GET = "GET";

    /**
     * `POST`请求
     */
    public static final String POST = "POST";

    /**
     * `PUT`请求
     */
    public static final String PUT = "PUT";

    /**
     * `DELETE`请求
     */
    public static final String DELETE = "DELETE";

    /**
     * 请求url
     */
    private String url;

    /**
     * 请求方法
     */
    private String method = GET;

    /**
     * 请求体
     */
    private String body;

    /**
     * 请求头
     */
    private Map<String, String> headers = new HashMap<>();

    /**
     * 当前请求第几次重试
     */
    private int retryTime;

    /**
     * 下次请求url回调
     */
    private Processor callback;

    /**
     * 回调传递参数
     */
    private Map<String, Object> props;

    public Request() {
    }

    public Request(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getRetryTime() {
        return retryTime;
    }

    public void setRetryTime(int retryTime) {
        this.retryTime = retryTime;
    }

    public Processor getCallback() {
        return callback;
    }

    public void setCallback(Processor callback) {
        this.callback = callback;
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }
}
