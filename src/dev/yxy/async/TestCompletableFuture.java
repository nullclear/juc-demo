package dev.yxy.async;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * CompletableFuture（异步模式）
 * <p>
 * Created by Nuclear on 2021/2/19
 */
public class TestCompletableFuture {

    /**
     * 1. CompletableFuture内部有默认的线程池，当然也可以传入线程池。
     * -------------------------------------------------- 分割线 --------------------------------------------------
     * {@link CompletableFuture#get()} 有两个作用：
     * 1. 获取异步线程内抛出的异常，如果不调用这个方法，那么异步线程中的异常则无法被感知。
     * 2. 阻塞主线程，如果异步线程的执行时间远大于主线程的执行时间，可以保证异步线程执行完再结束主线程。
     */
    @Test
    public void testVoid() throws ExecutionException, InterruptedException {
        // 没有返回值的异步回调，仅用于执行任务
        CompletableFuture<Void> async = CompletableFuture.runAsync(() -> {
            System.out.println("testVoid() [子线程]开始执行任务");
            try {
                TimeUnit.SECONDS.sleep(2);

                //todo 可以用get方法获取这里抛出的异常
                //int i = 1 / 0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("testVoid() [子线程]完成任务");
            }
        });

        System.out.println("testVoid() 主线程开始执行任务");
        TimeUnit.SECONDS.sleep(5);
        System.out.println("testVoid() 主线程完成任务");

        //todo 这里没有返回结果，那获取结果有什么用呢？
        async.get();

        System.out.println("testVoid() completed");
    }

    /**
     * 这个例子展示了有返回值的异步线程使用方法，不只是返回值，异常也会被保存下来，当你需要时再调用即可
     * 同时还有比较重要的Complete处理与exception处理方法的展示，还有其他方法，需要时再探索
     */
    @Test
    public void testResult() throws ExecutionException, InterruptedException {
        // 有返回值的异步回调
        CompletableFuture<Integer> async = CompletableFuture.supplyAsync(() -> {
            System.out.println("testResult() [子线程]开始执行任务");
            try {
                int i = 1 / 0;
                return 1024;
            } finally {
                System.out.println("testResult() [子线程]完成任务");
            }
        });

        System.out.println("testResult() 主线程开始执行任务");
        TimeUnit.SECONDS.sleep(5);
        System.out.println("testResult() 主线程完成任务");

        Integer result = async
                .whenComplete((integer, throwable) -> {
                    // integer可以为null
                    System.out.println("正常返回的信息 = " + integer);
                    // throwable可以为null，但是空指针异常会被拦截，不会抛出异常信息
                    //todo 如果supplyAsync()中没有异常，这里有异常，同样会输出404
                    System.out.println("异常信息 = " + throwable.getMessage());
                })
                .exceptionally(throwable -> 404)// 如果发生异常使用此结果
                .get();

        System.out.println("testResult() result = " + result);
    }
}