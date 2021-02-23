package dev.yxy.daemon;

import java.util.concurrent.TimeUnit;

/**
 * 模拟基于线程池的请求任务
 * Created by Nuclear on 2021/2/23
 */
public class RequestTask implements Runnable {

    @Override
    public void run() {
        Runtime.getRuntime().gc();

        try {
            // 开启守护线程
            DaemonContextHolder.start(new DaemonThread(new DaemonTask(), Thread.currentThread().getName()));

            // todo 在此自定义执行逻辑
            System.out.println("[子线程] " + Thread.currentThread().getName() + " 开始执行任务");
            try {
                TimeUnit.SECONDS.sleep(8);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("[子线程] " + Thread.currentThread().getName() + " 结束执行任务");

        } finally {
            // 假如子线程没死，一定会执行到这条语句，取消守护线程的任务，等着被GC线程回收
            // 假如子线程死了，守护线程也跟着死了，不需要执行这条语句，守护线程同样会被清除
            DaemonContextHolder.cancel();
        }
    }
}
