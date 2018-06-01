package com.xinqing.boy.core.pipeline;

import com.xinqing.boy.core.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 打印到日志
 *
 * @author xuan
 * @since 1.0.0
 */
public class ConsolePipeline implements Pipeline {

    private static final Logger LOG = LoggerFactory.getLogger(ConsolePipeline.class);

    private final int order = 0;

    @Override
    public void process(Item item) {
        if (item == null) {
            return;
        }
        item.getAll().forEach((key, value) -> LOG.info("{} -> {}", key, value));
    }

    @Override
    public int order() {
        return order;
    }

}
