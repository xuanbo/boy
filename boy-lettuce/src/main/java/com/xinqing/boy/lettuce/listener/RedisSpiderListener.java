package com.xinqing.boy.lettuce.listener;

import com.xinqing.boy.core.Request;
import com.xinqing.boy.core.Spider;
import com.xinqing.boy.core.listener.LocalSpiderListener;
import com.xinqing.boy.core.scheduler.Scheduler;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * redis实现监听器
 *
 * @author xuan
 * @since 1.0.0
 */
public class RedisSpiderListener extends LocalSpiderListener {

    private static final Logger LOG = LoggerFactory.getLogger(RedisSpiderListener.class);

    private static final String SUCCESS_PREFIX = "boy:listener:success:";
    private static final String ERROR_PREFIX = "boy:listener:error:";

    private final RedisClient client;

    public RedisSpiderListener(Scheduler scheduler, RedisClient client) {
        super(scheduler);
        this.client = client;
    }

    @Override
    public void init(Spider spider) {
        super.init(spider);

        // 连接
        StatefulRedisConnection<String, String> connection = client.connect();
        // 异步访问
        RedisAsyncCommands<String, String> asyncCommands = connection.async();
        asyncCommands.set(getSuccessPrefix(spider), "0");
        asyncCommands.set(getErrorPrefix(spider), "0");
        // 关闭
        connection.close();
    }

    @Override
    public void onSuccess(Spider spider, Request request) {
        super.onSuccess(spider, request);

        // 连接
        StatefulRedisConnection<String, String> connection = client.connect();
        // 异步访问
        RedisAsyncCommands<String, String> asyncCommands = connection.async();
        asyncCommands.incr(getSuccessPrefix(spider));
        // 关闭
        connection.close();
    }

    @Override
    public void onError(Spider spider, Request request) {
        super.onError(spider, request);

        // 连接
        StatefulRedisConnection<String, String> connection = client.connect();
        // 异步访问
        RedisAsyncCommands<String, String> asyncCommands = connection.async();
        asyncCommands.incr(getErrorPrefix(spider));
        // 关闭
        connection.close();
    }

    @Override
    public void onComplete(Spider spider) {
        // 连接
        StatefulRedisConnection<String, String> connection = client.connect();
        // 同步访问
        RedisCommands<String, String> syncCommands = connection.sync();
        String successNumber = syncCommands.get(getSuccessPrefix(spider));
        String errorNumber = syncCommands.get(getErrorPrefix(spider));
        // 关闭
        connection.close();

        if (successNumber == null) {
            successNumber = "0";
        }
        if (errorNumber == null) {
            errorNumber = "0";
        }

        LOG.info("spider[{}] report: success({}/{}), error({}/{})", spider.getName() + "@" + spider.getId(),
                successNumber, successCounter.get(), errorNumber, errorCounter.get());
    }

    private String getSuccessPrefix(Spider spider) {
        return SUCCESS_PREFIX + spider.getName();
    }

    private String getErrorPrefix(Spider spider) {
        return ERROR_PREFIX + spider.getName();
    }
}
