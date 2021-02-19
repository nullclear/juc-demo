package dev.yxy.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Future异步模式
 * Created by Nuclear on 2021/2/19
 */
public class Test {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //test01();
        test02();
    }

    private static void test01() throws ExecutionException, InterruptedException {
        // 没有返回值的异步回调，仅用于执行任务
        CompletableFuture<Void> async = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // 获取阻塞执行结果
        async.get();
        System.out.println("test01() completed");
    }

    private static void test02() throws ExecutionException, InterruptedException {
        // 有返回值的异步回调
        CompletableFuture<Integer> async = CompletableFuture.supplyAsync(() -> {
            int i = 1 / 0;
            return 1024;
        });

        Integer result = async
                .whenComplete((integer, throwable) -> {
                    // integer可以为null
                    System.out.println("正常返回的信息 = " + integer);
                    // throwable可以为null，但是空指针异常会被拦截，不会抛出异常信息
                    // 如果supplyAsync()中没有异常，这里有异常，同样会输出404
                    System.out.println("异常信息 = " + throwable.getMessage());
                })
                .exceptionally(throwable -> 404)// 如果发生异常使用此结果
                .get();

        System.out.println("test02() result = " + result);
    }
}