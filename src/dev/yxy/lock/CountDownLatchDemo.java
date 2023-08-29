package dev.yxy.lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 类似于减法计数器
 * 用于多个线程进行任务，一个线程等待
 * Created by Nuclear on 2020/12/21
 */
public class CountDownLatchDemo {

    public static void main(String[] args) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(10);
        ExecutorService exec = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 10; i++) {
            final int temp = i;
            exec.submit(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("任务" + temp + "执行完毕");
                latch.countDown();
            });
        }
        exec.shutdown();
        /*
         * 如果超时前，计数为0，则返回true.
         * 如果超时前，计数不为0，则返回false.
         */
        boolean await = latch.await(10, TimeUnit.SECONDS);
        System.out.printf("[%s]所有任务执行完毕%n", await);
    }
}
