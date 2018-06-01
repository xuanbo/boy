package com.xinqing.boy.lettuce;

import com.xinqing.boy.core.GithubProcessor;
import com.xinqing.boy.core.Spider;
import com.xinqing.boy.core.pipeline.TextPipeline;
import com.xinqing.boy.core.scheduler.Scheduler;
import com.xinqing.boy.lettuce.listener.RedisSpiderListener;
import com.xinqing.boy.lettuce.scheduler.RedisListScheduler;
import io.lettuce.core.RedisClient;
import org.junit.Test;

/**
 * @author xuan
 * @since 1.0.0
 */
public class GithubProcessorTest {

    @Test
    public void run() {
        // Redis Scheduler
        RedisClient client = RedisClient.create("redis://localhost/");
        RedisListScheduler scheduler = new RedisListScheduler(client);

        Spider.create(new GithubProcessor())
                .domain("github.com")
                .addTargetUrl("https://github.com/xuanbo")
                .addPipeline(new TextPipeline("D:\\developer\\Code\\boy\\github.txt"))
                // 替换默认的QueueScheduler
                .setScheduler(scheduler)
                // 添加RedisSpiderListener
                .addSpiderListener(new RedisSpiderListener(scheduler, client))
                .thread(5)
                .retry(1)
                .sleep(500)
                .run();
    }

}
