package com.xinqing.boy.processor;

import com.xinqing.boy.Response;
import com.xinqing.boy.Result;

/**
 * 处理器
 *
 * @author xuan
 * @since 1.0.0
 */
public interface Processor {

    /**
     * 解析Response
     *
     * @param response Response
     * @return Result
     */
    Result parse(Response response);

}
