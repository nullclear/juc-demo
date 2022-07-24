package dev.yxy.async;

import dev.yxy.util.SleepUtils;
import org.junit.Test;

/**
 * 测试 Synchronized 关键字
 *
 * @author atom
 * @create 2022/07/24 15:47
 * @update 2022/07/24 15:47
 * @origin juc-demo
 */
public class SyncTests {

    /**
     * Synchronized 获取锁的过程是无法被中断的，必须进入同步区域内
     */
    @Test
    public void test_whether_sync_lock_can_be_interrupted() {
        Object lock = new Object();

        Thread subThread = new Thread(() -> {
            synchronized (lock) {
                System.out.printf("Unfortunately, Thread is Interrupted = %s, But Still Acquired Sync Lock\n", Thread.currentThread().isInterrupted());
            }
        });

        synchronized (lock) {
            subThread.start();
            SleepUtils.millisecond(100);
            System.out.printf("Sub Thread State = %s, Sub Thread is Interrupted = %s\n", subThread.getState(), subThread.isInterrupted());
            subThread.interrupt();
            System.out.printf("Sub Thread State = %s, Sub Thread is Interrupted = %s\n", subThread.getState(), subThread.isInterrupted());
            SleepUtils.millisecond(100);
        }
        SleepUtils.second(1);
    }

    /**
     * {@link Object#wait()} 与 {@link Object#notifyAll()} 都是跟被监视的对象绑定的， 只能在对应的对象监视器范围内进行操作
     */
    @Test
    public void test_object_monitor_wait_and_notify() {
        Object lock = new Object();

        Thread subThread = new Thread(() -> {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException ignored) {
                }
                System.out.println("Sub Thread Has Been Notified");
            }
        });
        subThread.start();

        SleepUtils.second(1);
        synchronized (lock) {
            lock.notifyAll();
        }
        SleepUtils.second(1);
    }

}
