package dev.yxy.async;

import java.util.concurrent.TimeUnit;

/**
 * Created by atom on 2021/2/21
 */
public class TestDaemon {

    public static void main(String[] args) throws InterruptedException {
        Thread daemon = new Thread(new Task(), "守护线程");
        daemon.setDaemon(true);
        daemon.start();
        System.out.println("[主线程]开始任务");
        TimeUnit.SECONDS.sleep(10);
        System.out.println("[主线程]完成任务");
    }

    private static class Task implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    System.out.println(Thread.currentThread().getName() + "正在工作");
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
