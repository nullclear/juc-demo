package dev.yxy.async;

import dev.yxy.util.SleepUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @version 1.0
 * @since 2025-01-10
 */
public class ThreadInterruptTests {

    @Test
    public void test() {
        Thread thread = new Thread(() -> {
            Assert.assertFalse(Thread.currentThread().isInterrupted());
            LockSupport.park();// java.lang.Thread.interrupt()方法会唤醒线程，但是不会清除中断标志
            Assert.assertTrue(Thread.currentThread().isInterrupted());
            try {
                TimeUnit.MILLISECONDS.sleep(1);// 当中断标志为true时，会立即抛出InterruptedException异常
                Assert.fail();// 不会执行到这里
            } catch (InterruptedException e) {
                Assert.assertFalse(Thread.currentThread().isInterrupted());// 抛出InterruptedException异常后，中断标志会被清除
            }
        });
        thread.start();
        SleepUtils.millisecond(50);
        thread.interrupt();
        SleepUtils.second(1);
    }
}
