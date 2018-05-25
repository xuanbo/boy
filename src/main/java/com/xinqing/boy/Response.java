package com.xinqing.boy;

import com.xinqing.boy.processor.Processor;

import java.util.List;
import java.util.Map;

/**
 * 响应
 *
 * @author xuan
 * @since 1.0.0
 */
public class Response {

    /**
     * 客户端发起的请求错误码
     */
    private static final int CODE_CLIENT_ERROR = -1;

    /**
     * Http Ok状态码
     */
    private static final int CODE_OK = 200;

    /**
     * Http跳转状态码
     */
    private static final int CODE_OK_END = 300;

    /**
     * 响应码
     */
    private int statusCode;

    /**
     * 当前请求url
     */
    private String url;

    /**
     * 原始响应字节数组
     */
    private byte[] rawBytes;

    /**
     * 原始响应字符
     */
    private String rawText;

    /**
     * 响应头
     */
    private Map<String, List<String>> headers;

    /**
     * 回调
     */
    private Processor callback;

    /**
     * 传递给回调的参数
     */
    private Map<String, Object> props;

    public Response() {
    }

    /**
     * 响应失败
     *
     * @return Response
     */
    public static Response fail() {
        Response response = new Response();
        response.setStatusCode(CODE_CLIENT_ERROR);
        return response;
    }

    public Result createResult() {
        return Result.of(this);
    }

    /**
     * 判断响应是否成功，200~300之前认为成功
     *
     * @return boolean
     */
    public boolean isSuccess() {
        if (CODE_OK <= statusCode && statusCode < CODE_OK_END) {
            return true;
        }
        return false;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getUrl() {
        return url;
    }

    protected void setUrl(String url) {
        this.url = url;
    }

    public byte[] getRawBytes() {
        return rawBytes;
    }

    public void setRawBytes(byte[] rawBytes) {
        this.rawBytes = rawBytes;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
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
