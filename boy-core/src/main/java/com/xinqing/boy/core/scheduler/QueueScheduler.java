package com.xinqing.boy.core.scheduler;

import com.xinqing.boy.core.Request;
import com.xinqing.boy.core.Spider;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 队列调度器
 *
 * @author xuan
 * @since 1.0.0
 */
public class QueueScheduler extends DuplicateScheduler {

    /**
     * 内部任务队列
     */
    private Queue<Request> queue = new LinkedBlockingQueue<>();

    @Override
    public Request get(Spider spider) {
        return queue.poll();
    }

    @Override
    public int getLeft(Spider spider) {
        return queue.size();
    }

    @Override
    protected void pushWhenNoDuplicate(Spider spider, Request request) {
        queue.add(request);
    }

}
