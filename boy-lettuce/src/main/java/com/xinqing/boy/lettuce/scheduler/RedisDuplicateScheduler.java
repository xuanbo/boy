package com.xinqing.boy.lettuce.scheduler;

import com.xinqing.boy.core.Request;
import com.xinqing.boy.core.Spider;
import com.xinqing.boy.core.scheduler.DuplicateScheduler;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * 基于redis-set去重
 *
 * @author xuan
 * @since 1.0.0
 */
public abstract class RedisDuplicateScheduler extends DuplicateScheduler {

    /**
     * 存储所有爬取过的请求
     */
    private static final String PREFIX = "boy:all:";

    /**
     * redis客户端实例
     */
    protected final RedisClient client;

    protected RedisDuplicateScheduler(RedisClient client) {
        this.client = client;
    }

    @Override
    protected boolean isDuplicate(Spider spider, Request request) {
        // 连接
        StatefulRedisConnection<String, String> connection = client.connect();
        // 同步访问
        RedisCommands<String, String> syncCommands = connection.sync();
        // 添加到set
        Long result = syncCommands.sadd(getPrefix(spider), request.getUrl());
        // 关闭
        connection.close();

        // 为0，则重复添加元素
        if (result == 0) {
            return true;
        }
        return false;
    }

    @Override
    public int getTotal(Spider spider) {
        // 连接
        StatefulRedisConnection<String, String> connection = client.connect();
        // 同步访问
        RedisCommands<String, String> syncCommands = connection.sync();
        // 添加到set
        Long result = syncCommands.scard(getPrefix(spider));
        // 关闭
        connection.close();
        if (result == null) {
            return 0;
        }
        return result.intValue();
    }

    private String getPrefix(Spider spider) {
        return PREFIX + spider.getName();
    }

}
