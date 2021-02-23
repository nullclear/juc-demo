package dev.yxy.daemon;

/**
 * 守护线程执行的任务
 * Created by Nuclear on 2021/2/23
 */
public class DaemonTask implements Runnable {

    @Override
    public void run() {
        // todo 在此自定义执行逻辑
        System.out.println("[守护线程] - " + Thread.currentThread().getId() + " 正在守护任务 " + Thread.currentThread().getName());
    }
}
