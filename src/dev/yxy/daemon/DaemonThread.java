package dev.yxy.daemon;

import java.util.concurrent.TimeUnit;

/**
 * 守护线程
 * Created by Nuclear on 2021/2/23
 */
public class DaemonThread extends Thread implements Daemon {
    /**
     * 初始化延迟(ms)
     */
    private static final long INITIAL_DELAY = 500;

    /**
     * 单次循环时间(ms)
     */
    private static final long PERIOD = 2000;

    /**
     * 是否取消守护任务
     */
    private volatile boolean isCancel;

    {
        // 自动设置成守护线程
        this.setDaemon(true);
    }

    public DaemonThread(Runnable target, String name) {
        super(target, name);
    }

    public DaemonThread(Runnable target) {
        super(target);
    }

    @Override
    public void run() {
        // 初始化延迟
        try {
            TimeUnit.MILLISECONDS.sleep(INITIAL_DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 守护任务
        while (true) {
            if (this.isCancel) {
                break;
            } else {
                super.run();
                try {
                    TimeUnit.MILLISECONDS.sleep(PERIOD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void cancel() {
        isCancel = true;
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("[守护线程] - " + this.getId() + "/" + this.getName() + " 被GC清除了");
    }
}
