package com.example.boy;

import com.xinqing.boy.Spider;
import com.xinqing.boy.pipeline.ConsolePipeline;
import com.xinqing.boy.pipeline.TextPipeline;
import org.junit.Test;

/**
 * @author xuan
 * @since 1.0.0
 */
public class GithubProcessorTest {

    @Test
    public void run() {
        Spider.create(new GithubProcessor())
                .domain("github.com")
                .addTargetUrl("https://github.com/xuanbo")
                .addPipeline(new TextPipeline("D:\\developer\\Code\\boy\\github.txt"))
                .addPipeline(new ConsolePipeline())
                .thread(5)
                .retry(1)
                .sleep(500)
                .run();
    }

}
