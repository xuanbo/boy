package com.xinqing.boy.lettuce.scheduler;

import com.alibaba.fastjson.JSON;
import com.xinqing.boy.core.Request;
import com.xinqing.boy.core.Spider;
import com.xinqing.boy.core.processor.Processor;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于redis-list存储
 *
 * @author xuan
 * @since 1.0.0
 */
public class RedisListScheduler extends RedisDuplicateScheduler {

    /**
     * 缓存callback的Processor对象
     */
    private static final Map<String, Processor> PROCESS_CACHE = new ConcurrentHashMap<>();

    /**
     * 存储所有待处理的请求
     */
    private static final String PREFIX = "boy:queue:";

    public RedisListScheduler(RedisClient client) {
        super(client);
    }

    @Override
    protected void pushWhenNoDuplicate(Spider spider, Request request) {
        // 连接
        StatefulRedisConnection<String, String> connection = client.connect();
        // 同步访问
        RedisCommands<String, String> syncCommands = connection.sync();
        // 放入list尾部
        syncCommands.rpush(getPrefix(spider), JSON.toJSONString(request));
        // 关闭
        connection.close();
    }

    @Override
    public Request get(Spider spider) {
        // 连接
        StatefulRedisConnection<String, String> connection = client.connect();
        // 同步访问
        RedisCommands<String, String> syncCommands = connection.sync();
        // 从list头部获取
        String data = syncCommands.lpop(getPrefix(spider));
        Request request = null;
        if (data != null) {
            request = JSON.parseObject(data, Request.class);
            // 还原或者从缓存中获取callback对象
            instanceOrGetCachedCallback(request);
        }
        // 关闭
        connection.close();
        return request;
    }

    /**
     * 还原或者从缓存中获取callback对象
     *
     * @param request Request
     */
    private void instanceOrGetCachedCallback(Request request) {
        String callbackClazz = request.getCallbackClazz();
        Processor processor = PROCESS_CACHE.get(callbackClazz);
        // 本地缓存中不存在，则反射放入缓存
        if (processor == null) {
            try {
                Class<?> clazz = Class.forName(callbackClazz);
                Object instance = clazz.newInstance();
                if (instance instanceof Processor) {
                    processor = (Processor) instance;
                    PROCESS_CACHE.put(callbackClazz, processor);
                } else {
                    throw new IllegalStateException(callbackClazz + "类型必须是" + Processor.class.getName());
                }
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new IllegalStateException(e);
            }
        }
        request.callback(processor);
    }

    @Override
    public int getLeft(Spider spider) {
        // 连接
        StatefulRedisConnection<String, String> connection = client.connect();
        // 同步访问
        RedisCommands<String, String> syncCommands = connection.sync();
        Long len = syncCommands.llen(getPrefix(spider));
        // 关闭
        connection.close();

        if (len == null) {
            return 0;
        }
        return len.intValue();
    }

    private String getPrefix(Spider spider) {
        return PREFIX + spider.getName();
    }

}
