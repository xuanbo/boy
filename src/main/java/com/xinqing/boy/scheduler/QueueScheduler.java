package com.xinqing.boy.scheduler;

import com.xinqing.boy.Request;

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
    public Request get() {
        return queue.poll();
    }

    @Override
    public int getLeft() {
        return queue.size();
    }

    @Override
    protected void pushWhenNoDuplicate(Request request) {
        queue.add(request);
    }

}
