package dev.yxy.async;

import dev.yxy.util.SleepUtils;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
        CompletableFuture<Void> async = CompletableFuture
                .runAsync(() -> {
                    System.out.println("testVoid() [子线程]开始任务");
                    SleepUtils.second(2);
                    // NOTE - 2021/10/28 可以用get方法获取这里抛出的异常
                    // throwException();
                    System.out.println("testVoid() [子线程]完成任务");
                });

        System.out.println("testVoid() 主线程开始任务");
        SleepUtils.second(5);
        System.out.println("testVoid() 主线程完成任务");

        // NOTE - 2021/10/28 这里没有返回结果，那获取结果有什么用呢？ 见方法注释
        async.get();

        System.out.println("testVoid() last");
    }

    /**
     * 提供返回值的异步调度
     */
    @Test
    public void testSupply() throws ExecutionException, InterruptedException {
        // 有返回值的异步回调
        CompletableFuture<Integer> async = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        System.out.println("testSupply() [子线程]开始任务");
                        throwException();
                        return 1024;
                    } finally {
                        System.out.println("testSupply() [子线程]完成任务");
                    }
                });

        System.out.println("testSupply() 主线程开始任务");
        SleepUtils.second(5);
        System.out.println("testSupply() 主线程完成任务");

        Integer result = async
                .whenComplete((i, ex) -> {
                    // i 可能是null
                    System.out.println("正常返回的信息 = " + i);

                    // 如果supplyAsync()中没有异常，这里有异常（比如ex是null），同样会输出404
                    System.out.println("异常信息 = " + ex.getMessage());
                })
                .exceptionally(throwable -> 404)// 处理异常，相当于Spring的ExceptionHandler
                .get();

        System.out.println("testSupply() result = " + result);
    }

    private static void throwException() {
        throw new AssertionError("断言错误");
    }
}