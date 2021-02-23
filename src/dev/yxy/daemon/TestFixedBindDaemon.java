package dev.yxy.daemon;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nuclear on 2021/2/23
 */
public class TestFixedBindDaemon {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            pool.execute(new RequestTask());
        }
        pool.shutdown();
        pool.awaitTermination(200, TimeUnit.SECONDS);
    }
}
