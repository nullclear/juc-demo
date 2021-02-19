package dev.yxy.singleton;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nuclear on 2021/2/19
 */
public class TestConnection {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService exec = Executors.newFixedThreadPool(60);
        CyclicBarrier barrier = new CyclicBarrier(50);
        for (int i = 0; i < 100; i++) {
            exec.execute(() -> {
                try {
                    //等下后面的线程一起执行，模拟并发情况
                    barrier.await();
                    Connection instance = Connection.getInstance();
                    System.out.println("instance = " + instance);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        exec.shutdown();
        exec.awaitTermination(10, TimeUnit.SECONDS);
    }
}
