/*
 *  Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xinqing.boy.util;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * <pre>
 * 
 * 代码和思路主要来自于：
 * 
 * tomcat : 
 * 		org.apache.catalina.core.StandardThreadExecutor
 * 
 * java.util.concurrent
 * threadPoolExecutor execute执行策略： 		优先offer到queue，queue满后再扩充线程到maxThread，如果已经到了maxThread就reject 
 * 						   		比较适合于CPU密集型应用（比如runnable内部执行的操作都在JVM内部，memory copy, or compute等等）
 * 
 * StandardThreadExecutor execute执行策略：	优先扩充线程到maxThread，再offer到queue，如果满了就reject 
 * 						      	比较适合于业务处理需要远程资源的场景
 * 
 * </pre>
 * 
 * @author maijunsheng
 * @version 创建时间：2013-6-20
 *
 * 注：代码来源于motan，地址：https://github.com/weibocom/motan/blob/master/motan-core/src/main/java/com/weibo/api/motan/core/StandardThreadExecutor.java
 */
public class StandardThreadExecutor extends ThreadPoolExecutor {

	public static final int DEFAULT_MIN_THREADS = 20;
	public static final int DEFAULT_MAX_THREADS = 200;

	/**
	 * 1 minutes
	 */
	public static final int DEFAULT_MAX_IDLE_TIME = 60 * 1000;

	/**
	 * 正在处理的任务数
	 */
	protected AtomicInteger submittedTasksCount;

	/**
	 * 最大允许同时处理的任务数
	 */
	private int maxSubmittedTaskCount;

	public StandardThreadExecutor() {
		this(DEFAULT_MIN_THREADS, DEFAULT_MAX_THREADS);
	}

	public StandardThreadExecutor(int coreThread, int maxThreads) {
		this(coreThread, maxThreads, maxThreads);
	}

	public StandardThreadExecutor(int coreThread, int maxThreads, long keepAliveTime, TimeUnit unit) {
		this(coreThread, maxThreads, keepAliveTime, unit, maxThreads);
	}

	public StandardThreadExecutor(int coreThreads, int maxThreads, int queueCapacity) {
		this(coreThreads, maxThreads, queueCapacity, new DefaultThreadFactory());
	}

	public StandardThreadExecutor(int coreThreads, int maxThreads, int queueCapacity, ThreadFactory threadFactory) {
		this(coreThreads, maxThreads, DEFAULT_MAX_IDLE_TIME, TimeUnit.MILLISECONDS, queueCapacity, threadFactory);
	}

	public StandardThreadExecutor(int coreThreads, int maxThreads, long keepAliveTime, TimeUnit unit, int queueCapacity) {
		this(coreThreads, maxThreads, keepAliveTime, unit, queueCapacity, new DefaultThreadFactory());
	}

	public StandardThreadExecutor(int coreThreads, int maxThreads, long keepAliveTime, TimeUnit unit,
			int queueCapacity, ThreadFactory threadFactory) {
		this(coreThreads, maxThreads, keepAliveTime, unit, queueCapacity, threadFactory, new AbortPolicy());
	}

	public StandardThreadExecutor(int coreThreads, int maxThreads, long keepAliveTime, TimeUnit unit,
			int queueCapacity, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(coreThreads, maxThreads, keepAliveTime, unit, new ExecutorQueue(), threadFactory, handler);
		((ExecutorQueue) getQueue()).setStandardThreadExecutor(this);

		submittedTasksCount = new AtomicInteger(0);
		
		// 最大并发任务限制： 队列buffer数 + 最大线程数 
		maxSubmittedTaskCount = queueCapacity + maxThreads; 
	}

	@Override
	public void execute(Runnable command) {
		int count = submittedTasksCount.incrementAndGet();

		// 超过最大的并发任务限制，进行 reject
		// 依赖的LinkedTransferQueue没有长度限制，因此这里进行控制 
		if (count > maxSubmittedTaskCount) {
			submittedTasksCount.decrementAndGet();
			getRejectedExecutionHandler().rejectedExecution(command, this);
		}

		try {
			super.execute(command);
		} catch (RejectedExecutionException rx) {
			// there could have been contention around the queue
			if (!((ExecutorQueue) getQueue()).force(command)) {
				submittedTasksCount.decrementAndGet();

				getRejectedExecutionHandler().rejectedExecution(command, this);
			}
		}
	}

	public int getSubmittedTasksCount() {
		return this.submittedTasksCount.get();
	}
	
	public int getMaxSubmittedTaskCount() {
		return maxSubmittedTaskCount;
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		submittedTasksCount.decrementAndGet();
	}
}

/**
 * LinkedTransferQueue 能保证更高性能，相比与LinkedBlockingQueue有明显提升 
 * 
 * <pre>
 * 		1) 不过LinkedTransferQueue的缺点是没有队列长度控制，需要在外层协助控制
 * </pre>
 * 
 * @author maijunsheng
 *
 * 注：代码来源于motan，地址：https://github.com/weibocom/motan/blob/master/motan-core/src/main/java/com/weibo/api/motan/core/DefaultThreadFactory.java
 */
class ExecutorQueue extends LinkedTransferQueue<Runnable> {

	private static final long serialVersionUID = -265236426751004839L;

    /**
     * 当前线程池实例
     */
	private StandardThreadExecutor threadPoolExecutor;

	public ExecutorQueue() {
		super();
	}

	public void setStandardThreadExecutor(StandardThreadExecutor threadPoolExecutor) {
		this.threadPoolExecutor = threadPoolExecutor;
	}

	public boolean force(Runnable o) {
		if (threadPoolExecutor.isShutdown()) {
			throw new RejectedExecutionException("Executor not running, can't force a command into the queue");
		}
		// forces the item onto the queue, to be used if the task is rejected
		return super.offer(o);
	}

	@Override
	public boolean offer(Runnable o) {
		int poolSize = threadPoolExecutor.getPoolSize();

		// we are maxed out on threads, simply queue the object
		if (poolSize == threadPoolExecutor.getMaximumPoolSize()) {
			return super.offer(o);
		}
		// we have idle threads, just add it to the queue
		// note that we don't use getActiveCount(), see BZ 49730
		if (threadPoolExecutor.getSubmittedTasksCount() <= poolSize) {
			return super.offer(o);
		}
		// if we have less threads than maximum force creation of a new
		// thread
		// 主要是这里，当线程没有到达最大数量，则返回false，则workQueue offer失败，从而会创建线程池。
		if (poolSize < threadPoolExecutor.getMaximumPoolSize()) {
			return false;
		}
		// if we reached here, we need to add it to the queue
		return super.offer(o);
	}
}