package dev.yxy.condition.water;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 用 {@link Condition} 模拟 {@link Semaphore}
 * Created by Nuclear on 2021/2/24
 */
@SuppressWarnings("DanglingJavadoc")
public class WaterFakeSemaphore implements IWater {
    private ReentrantLock lock = new ReentrantLock();
    private Condition h = lock.newCondition();
    private Condition o = lock.newCondition();
    private AtomicInteger countH = new AtomicInteger(2);
    private AtomicInteger countO = new AtomicInteger(0);

    @Override
    public void provideH(Runnable runnable) {
        lock.lock();
        try {
            /**
             * 类似于 {@link Semaphore#acquire()}
             */
            {
                while ((countH.get() - 1) < 0) {
                    h.await();
                }
                countH.decrementAndGet();
            }

            runnable.run();

            /**
             * 类似于 {@link Semaphore#release()}
             */
            {
                countO.incrementAndGet();
                o.signalAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void provideO(Runnable runnable) {
        lock.lock();
        try {
            /**
             * 类似于 {@link Semaphore#acquire()}
             */
            {
                while ((countO.get() - 2) < 0) {
                    o.await();
                }
                countO.addAndGet(-2);
            }

            runnable.run();

            /**
             * 类似于 {@link Semaphore#release()}
             */
            {
                countH.addAndGet(2);
                h.signalAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void release() {
        lock.lock();
        try {
            o.signalAll();
            h.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
