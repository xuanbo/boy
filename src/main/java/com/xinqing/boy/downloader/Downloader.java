package com.xinqing.boy.downloader;

import com.xinqing.boy.Request;
import com.xinqing.boy.Response;

/**
 * 下载
 *
 * @author xuan
 * @since 1.0.0
 */
public interface Downloader {

    /**
     * 执行下载
     *
     * @param request Request
     * @return Response
     */
    Response download(Request request);

}
