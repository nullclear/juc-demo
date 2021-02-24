package dev.yxy.order;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 用 {@link Condition} 精准唤醒控制顺序也行
 * 其实普通的 {@link #wait()} 也行，就算有虚假唤醒，也有while控制着
 * Created by atom on 2021/2/24
 */
public class DataCondition implements IData {
    private volatile int num = 0;
    private ReentrantLock lock = new ReentrantLock();
    private Condition conditionA = lock.newCondition();
    private Condition conditionB = lock.newCondition();
    private Condition conditionC = lock.newCondition();

    @Override
    public void printA(Runnable runnable) {
        for (int i = 0; i < 10; i++) {
            lock.lock();
            try {
                while (num != 0) {
                    conditionA.await();
                }
                runnable.run();
                num = 1;
                conditionB.signal();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void printB(Runnable runnable) {
        for (int i = 0; i < 10; i++) {
            lock.lock();
            try {
                while (num != 1) {
                    conditionB.await();
                }
                runnable.run();
                num = 2;
                conditionC.signal();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void printC(Runnable runnable) {
        for (int i = 0; i < 10; i++) {
            lock.lock();
            try {
                while (num != 2) {
                    conditionC.await();
                }
                runnable.run();
                num = 0;
                conditionA.signal();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
}
