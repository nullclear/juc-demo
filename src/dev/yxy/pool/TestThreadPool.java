package dev.yxy.pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义线程池可以更好的掌控线程池
 * ----
 * 四种线程池
 * 七大参数
 * 四大拒绝策略
 * Created by Nuclear on 2021/2/19
 */
public class TestThreadPool {

    /**
     * 首先明确一点，所有的RejectedExecutionHandler都是在主线程中执行的，策略的详细实现看源码
     * ----
     * new ThreadPoolExecutor.AbortPolicy() // 阻塞队列满了，直接抛出异常 - RejectedExecutionException
     * new ThreadPoolExecutor.CallerRunsPolicy() // 由调用者（主线程）执行此任务
     * new ThreadPoolExecutor.DiscardPolicy() // 阻塞队列满了，丢掉此任务，不会抛出异常（实际上源码中是空实现）
     * new ThreadPoolExecutor.DiscardOldestPolicy() // 队列满了，丢弃阻塞队列中队首的一个Task，然后将此任务加入其中
     */
    public static void main(String[] args) throws InterruptedException {
        int corePoolSize = 1;
        int maximumPoolSize = 3;
        long keepAliveTime = 60;
        int capacity = 3;

        // 核心线程数量1，最大线程数量3，阻塞队列容量3，最多可以接受6个任务，采用丢弃策略，不能执行的任务全部静默丢弃
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<>(capacity), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
        executor.allowCoreThreadTimeOut(true);

        for (int i = 0; i < 20; i++) {
            int temp = i;
            executor.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println(temp);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }
}
