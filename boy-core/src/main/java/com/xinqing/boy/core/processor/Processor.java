package com.xinqing.boy.core.processor;

import com.xinqing.boy.core.Response;
import com.xinqing.boy.core.Result;

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
