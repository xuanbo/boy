package com.xinqing.boy.pipeline;

import com.xinqing.boy.Item;

/**
 * 管道
 *
 * @author xuan
 * @since 1.0.0
 */
public interface Pipeline {

    /**
     * 处理Item
     *
     * @param item Item
     */
    void process(Item item);

    /**
     * 排序，值越小优先级越高
     *
     * @return int
     */
    int order();

}
