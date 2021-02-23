package dev.yxy.daemon;

/**
 * 守护线程接口
 * Created by Nuclear on 2021/2/23
 */
public interface Daemon {

    /**
     * 启动守护线程
     */
    void start();

    /**
     * 取消守护线程的任务
     */
    void cancel();
}
