package com.xinqing.boy;

import com.xinqing.boy.downloader.Downloader;
import com.xinqing.boy.downloader.OkHttpClientDownloader;
import com.xinqing.boy.listener.LocalSpiderListener;
import com.xinqing.boy.listener.SpiderListener;
import com.xinqing.boy.middleware.Middleware;
import com.xinqing.boy.pipeline.Pipeline;
import com.xinqing.boy.processor.Processor;
import com.xinqing.boy.scheduler.QueueScheduler;
import com.xinqing.boy.scheduler.Scheduler;
import com.xinqing.boy.util.DefaultThreadFactory;
import com.xinqing.boy.util.StandardThreadExecutor;
import com.xinqing.boy.util.UrlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 爬虫
 *
 * @author xuan
 * @since 1.0.0
 */
public class Spider {
    
    private static final Logger LOG = LoggerFactory.getLogger(Spider.class);

    /**
     * 爬虫名
     */
    private String name;

    /**
     * 爬虫监听器
     */
    private List<SpiderListener> listeners = new ArrayList<>();

    /**
     * 下载器
     */
    private Downloader downloader = new OkHttpClientDownloader();

    /**
     * 处理器
     */
    private Processor processor;

    /**
     * 中间件
     */
    private List<Middleware> middlewares = new ArrayList<>();

    /**
     * 管道
     */
    private List<Pipeline> pipelines = new ArrayList<>();

    /**
     * 调度器
     */
    private Scheduler scheduler = new QueueScheduler();

    /**
     * 开始url
     */
    private List<String> targetUrls = new ArrayList<>();

    /**
     * 内部线程池
     */
    private ThreadPoolExecutor poolExecutor;

    /**
     * 内部线程池线程数量
     */
    private int threadNum = 2;

    /**
     * 内部线程池队列大小
     */
    private int queueSize = 1000;

    /**
     * 爬虫运行标志
     */
    private AtomicBoolean running = new AtomicBoolean(false);

    /**
     * 等待新的url时间
     */
    private final int waitNewUrlSeconds = 3;

    /**
     * 等待新的url lock
     */
    private final ReentrantLock newUrlLock = new ReentrantLock();

    /**
     * 等待新的url condition
     */
    private final Condition newUrlCondition = newUrlLock.newCondition();

    /**
     * 请求失败重试次数
     */
    private int retryNum = 3;

    /**
     * 爬虫请求等待时间，单位微秒
     */
    private int sleepMilliseconds = 500;

    /**
     * 站点domain，为空则不限制
     */
    private String domain;

    public Spider(Processor processor) {
        this.processor = processor;
    }

    /**
     * 根据Processor创建一个Spider
     *
     * @param processor Processor
     * @return Spider
     */
    public static Spider create(Processor processor) {
        return new Spider(processor);
    }

    /**
     * 设置爬虫名
     *
     * @param name 爬虫名称
     * @return Spider
     */
    public Spider name(String name) {
        checkIfRunning();
        this.name = name;
        return this;
    }

    /**
     * 添加监听器
     *
     * @param listener SpiderListener
     * @return Spider
     */
    public Spider addSpiderListener(SpiderListener listener) {
        checkIfRunning();
        this.listeners.add(listener);
        return this;
    }

    /**
     * 设置监听器
     *
     * @param listeners List<SpiderListener>
     * @return Spider
     */
    public Spider setSpiderListeners(List<SpiderListener> listeners) {
        checkIfRunning();
        this.listeners = listeners;
        return this;
    }

    /**
     * 设置下载器
     *
     * @param downloader Downloader
     * @return Spider
     */
    public Spider setDownloader(Downloader downloader) {
        checkIfRunning();
        this.downloader = downloader;
        return this;
    }

    /**
     * 添加中间件
     *
     * @param middleware Middleware
     * @return Spider
     */
    public Spider addMiddleware(Middleware middleware) {
        checkIfRunning();
        this.middlewares.add(middleware);
        return this;
    }

    /**
     * 设置中间件
     *
     * @param middlewares List<Middleware>
     * @return Spider
     */
    public Spider setMiddlewares(List<Middleware> middlewares) {
        checkIfRunning();
        this.middlewares = middlewares;
        return this;
    }

    /**
     * 添加管道
     *
     * @param pipeline Pipeline
     * @return Spider
     */
    public Spider addPipeline(Pipeline pipeline) {
        checkIfRunning();
        this.pipelines.add(pipeline);
        return this;
    }

    /**
     * 设置管道
     *
     * @param pipelines List<Pipeline>
     * @return Spider
     */
    public Spider setPipelines(List<Pipeline> pipelines) {
        checkIfRunning();
        this.pipelines = pipelines;
        return this;
    }

    /**
     * 设置调度器
     *
     * @param scheduler Scheduler
     * @return Spider
     */
    public Spider setScheduler(Scheduler scheduler) {
        checkIfRunning();
        this.scheduler = scheduler;
        return this;
    }

    /**
     * 添加开始url
     *
     * @param url 开始url
     * @return Spider
     */
    public Spider addTargetUrl(String url) {
        checkIfRunning();
        targetUrls.add(url);
        return this;
    }

    /**
     * 设置开始urls
     *
     * @param targetUrls 开始urls
     * @return Spider
     */
    public Spider setTargetUrls(List<String> targetUrls) {
        checkIfRunning();
        this.targetUrls = targetUrls;
        return this;
    }

    /**
     * 设置线程数
     *
     * @param num 线程数
     * @return Spider
     */
    public Spider thread(int num) {
        checkIfRunning();
        this.threadNum = num;
        return this;
    }

    /**
     * 设置线程池
     *
     * @param poolExecutor ThreadPoolExecutor
     * @return Spider
     */
    public Spider thread(ThreadPoolExecutor poolExecutor) {
        checkIfRunning();
        this.poolExecutor = poolExecutor;
        return this;
    }

    /**
     * 设置线程池任务队列大小
     *
     * @param num 任务队列大小
     * @return Spider
     */
    public Spider queue(int num) {
        checkIfRunning();
        this.queueSize = num;
        return this;
    }

    /**
     * 设置请求失败后重试次数
     *
     * @param num 重试次数
     * @return Spider
     */
    public Spider retry(int num) {
        checkIfRunning();
        if (num < 0) {
            throw new IllegalStateException("retry num must gt zero.");
        }
        this.retryNum = num;
        return this;
    }

    /**
     * 设置请求间隔时间
     *
     * @param milliseconds 间隔时间
     * @return Spider
     */
    public Spider sleep(int milliseconds) {
        checkIfRunning();
        if (milliseconds < 0) {
            throw new IllegalStateException("sleep milliseconds must gt zero.");
        }
        this.sleepMilliseconds = milliseconds;
        return this;
    }

    /**
     * 设置domain
     *
     * @param domain domain
     * @return Spider
     */
    public Spider domain(String domain) {
        checkIfRunning();
        this.domain = domain;
        return this;
    }

    /**
     * 运行爬虫
     */
    public void run() {
        checkIfRunning();
        initSpider();
        while (running.get()) {
            Request request = scheduler.get();
            if (request == null) {
                if (poolExecutor.getActiveCount() == 0) {
                    break;
                }
                waitForNewUrl();
            } else {
                poolExecutor.execute(() -> {
                    try {
                        processRequest(request);
                    } catch (Exception e) {
                        LOG.warn("process request {} error", request.getUrl(), e);
                    } finally {
                        signalNewUrl();
                    }
                });
            }
        }
        close();
    }

    /**
     * 关闭爬虫
     */
    public void stop() {
        if (running.get()) {
            running.getAndSet(false);
        }
    }

    /**
     * 检查时候运行，运行则抛出IllegalStateException
     */
    private void checkIfRunning() {
        if (running.get()) {
            throw new IllegalStateException("spider is running.");
        }
    }

    /**
     * 初始化爬虫
     */
    private void initSpider() {
        running.getAndSet(true);
        LOG.info("start spider at domain[{}].", domain == null ? "*" : domain);
        spiderName();
        toRequests(targetUrls).forEach(scheduler::push);
        initListener();
        sortMiddlewares();
        sortPipelines();
        initThreadPool();
    }

    /**
     * 初始化爬虫名称
     */
    private void spiderName() {
        if (name == null) {
            name = processor.getClass().getName();
        }
    }

    /**
     * 将开始url转化为请求
     *
     * @param targetUrl 开始url
     * @return Request
     */
    private Request toRequest(String targetUrl) {
        return new Request(targetUrl);
    }

    /**
     * 将开始urls转化为请求
     *
     * @param targetUrls 开始urls
     * @return List<Request>
     */
    private List<Request> toRequests(List<String> targetUrls) {
        return targetUrls.stream().map(Request::new).collect(Collectors.toList());
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        if (listeners.isEmpty()) {
            listeners.add(new LocalSpiderListener(scheduler));
        }
        listeners.forEach(listener -> listener.init(name));
    }

    /**
     * 排序中间件
     */
    private void sortMiddlewares() {
        if (middlewares.isEmpty()) {
            LOG.warn("no middlewares!!!");
        } else {
            // sort pipeline
            middlewares.sort(Comparator.comparing(Middleware::order));
            LOG.info("middlewares:");
            LOG.info("[");
            middlewares.forEach(middleware -> LOG.info("{}", middleware.getClass().getName()));
            LOG.info("]");
        }
    }

    /**
     * 排序管道
     */
    private void sortPipelines() {
        if (pipelines.isEmpty()) {
            LOG.warn("no pipelines!!!");
        } else {
            // sort pipeline
            pipelines.sort(Comparator.comparing(Pipeline::order));
            LOG.info("pipelines:");
            LOG.info("[");
            pipelines.forEach(pipeline -> LOG.info("{}", pipeline.getClass().getName()));
            LOG.info("]");
        }
    }

    /**
     * 初始化线程池
     */
    private void initThreadPool() {
        if (poolExecutor == null) {
            poolExecutor = new StandardThreadExecutor(threadNum, threadNum, 3, TimeUnit.SECONDS, queueSize,
                    new DefaultThreadFactory("spider", true), new ThreadPoolExecutor.CallerRunsPolicy());
        }
    }

    /**
     * 等待新的url，等待时间：waitNewUrlSeconds
     */
    private void waitForNewUrl() {
        newUrlLock.lock();
        try {
            //double check
            if (poolExecutor.getActiveCount() == 0) {
                return;
            }
            newUrlCondition.await(waitNewUrlSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.warn("wait new url interrupted.", e);
        } finally {
            newUrlLock.unlock();
        }
    }

    /**
     * 处理请求
     *
     * @param request Request
     */
    private void processRequest(Request request) {
        // 限制请求
        if (!checkDomain(request)) {
            return;
        }
        // 中间件
        middlewares.forEach(middleware -> middleware.process(request));
        // 下载
        Response response = downloader.download(request);
        response.setUrl(request.getUrl());
        if (response.isSuccess()) {
            // 监听器
            listeners.forEach(listener -> listener.onSuccess(request));
            // 解析
            Result result = processor.parse(response);
            if (result == null) {
                LOG.info("download {}[{}] success, result is None.", request.getUrl(), response.getStatusCode());
                return;
            } else {
                LOG.info("download {}[{}] success.", request.getUrl(), response.getStatusCode());
            }
            toRequests(result.getNextUrls()).forEach(scheduler::push);
            // 管道
            pipelines.forEach(pipeline -> pipeline.process(result.getItem()));
            // 请求等待
            requestSleep();
        } else {
            LOG.warn("download {}[{}] error", request.getUrl(), response.getStatusCode());
            // 监听器
            listeners.forEach(listener -> listener.onError(request));
            // 重试
            retryRequest(request);
        }

    }

    private boolean checkDomain(Request request) {
        if (domain == null) {
            return true;
        }
        String domain = UrlUtil.getDomain(request.getUrl());
        if (this.domain.equals(domain)) {
            return true;
        }
        LOG.warn("discard {} at domain[{}]", request.getUrl(), domain);
        return false;
    }

    /**
     * 请求等待
     */
    private void requestSleep() {
        try {
            Thread.sleep(sleepMilliseconds);
        } catch (InterruptedException e) {
            LOG.error("thread interrupted when sleep.", e);
        }
    }

    /**
     * 请求重试
     *
     * @param request Request
     */
    private void retryRequest(Request request) {
        int retryTime = request.getRetryTime();
        if (retryTime < retryNum) {
            retryTime++;
            request.setRetryTime(retryTime);
            scheduler.push(request);
        }
    }

    /**
     * 通知新的url
     */
    private void signalNewUrl() {
        try {
            newUrlLock.lock();
            newUrlCondition.signalAll();
        } finally {
            newUrlLock.unlock();
        }
    }

    /**
     * 关闭
     */
    private void close() {
        poolExecutor.shutdown();
        running.getAndSet(false);
        listeners.forEach(SpiderListener::onComplete);
        listeners.forEach(SpiderListener::destroy);
        LOG.info("spider stop.");
    }
}
