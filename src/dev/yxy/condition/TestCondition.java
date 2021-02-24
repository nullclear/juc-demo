package dev.yxy.condition;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * {@link ReentrantLock} 的定义如下，和synchronized具有相同功能，但是有扩展功能。
 * A reentrant mutual exclusion {@link Lock} with the same basic behavior and semantics
 * as the implicit monitor lock accessed using {@code synchronized} methods and statements,
 * but with extended capabilities.
 * ----
 * 扩展功能就是 {@link Condition} 可以<b>精准唤醒</b>等待的线程，而不是和 {@link #notifyAll()} 一样唤醒全部等待线程
 * Created by Nuclear on 2021/2/24
 */
public class TestCondition {
    private ReentrantLock lock = new ReentrantLock();
    private Condition a = lock.newCondition();
    private Condition b = lock.newCondition();

    public static void main(String[] args) throws InterruptedException {
        TestCondition testCondition = new TestCondition();
        new Thread(testCondition::awaitA).start();
        new Thread(testCondition::awaitA).start();
        new Thread(testCondition::awaitB).start();
        new Thread(testCondition::awaitB).start();

        //先唤醒A
        TimeUnit.SECONDS.sleep(3);
        testCondition.releaseA();

        //再唤醒B
        TimeUnit.SECONDS.sleep(3);
        testCondition.releaseB();
    }

    public void awaitA() {
        lock.lock();
        try {
            System.out.println("A await");
            a.await();
            System.out.println("A finish");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void awaitB() {
        lock.lock();
        try {
            System.out.println("B await");
            b.await();
            System.out.println("B finish");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 这个加锁解锁就起到一个监视器的作用，其他没什么用
     */
    public void releaseA() {
        lock.lock();
        try {
            a.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void releaseB() {
        lock.lock();
        try {
            b.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
