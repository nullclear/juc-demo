package dev.yxy.buffer.lock;


import dev.yxy.buffer.Cake;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * lock替代了synchronized作为监视器
 * ReentrantLock不能跨线程使用，所以在不同线程中使用lock没有问题
 * Created by Nuclear on 2020/12/29
 */
public class CakeBuffer {
    private static final int MAX = 20;
    private ConcurrentLinkedQueue<Cake> queue = new ConcurrentLinkedQueue<>();

    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void push(Cake cake) {
        try {
            lock.lock();
            //如果生产达到了上限就等待
            while (queue.size() == MAX) {
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //线程被唤醒了就继续生产蛋糕
            if (queue.offer(cake)) {
                System.out.println("厨师[生产]了蛋糕" + cake.getId());
                //通知等待的顾客
                condition.signalAll();
            } else {
                System.out.println("[ERROR]厨师生成蛋糕失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public synchronized void pop() {
        try {
            lock.lock();
            //如果蛋糕吃完了就等待
            while (queue.isEmpty()) {
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //线程被唤醒了就继续消费蛋糕
            Cake cake = queue.poll();
            if (cake != null) {
                System.out.println("顾客 [" + Thread.currentThread().getName() + "] 已经 [消费]了蛋糕" + cake.getId());
                //通知等待的厨师
                condition.signalAll();
            } else {
                System.out.println(Thread.currentThread().getName() + " - [ERROR]虚假唤醒");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
