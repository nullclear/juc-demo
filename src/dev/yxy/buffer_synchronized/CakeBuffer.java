package dev.yxy.buffer_synchronized;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 用while替代if可以解决虚假唤醒的问题
 * Created by Nuclear on 2020/12/29
 */
public class CakeBuffer {
    private static final int MAX = 20;
    private ConcurrentLinkedQueue<Cake> queue = new ConcurrentLinkedQueue<>();

    public synchronized void push(Cake cake) {
        //如果生产达到了上限就等待
        while (queue.size() == MAX) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //线程被唤醒了就继续生产蛋糕
        if (queue.offer(cake)) {
            System.out.println("厨师[生产]了蛋糕" + cake.getId());
            //通知等待的顾客
            this.notifyAll();
        } else {
            System.out.println("[ERROR]厨师生成蛋糕失败");
        }
    }

    public synchronized void pop() {
        //如果蛋糕吃完了就等待
        while (queue.isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //线程被唤醒了就继续消费蛋糕
        Cake cake = queue.poll();
        if (cake != null) {
            System.out.println("顾客 [" + Thread.currentThread().getName() + "] 已经 [消费]了蛋糕" + cake.getId());
            //通知等待的厨师
            this.notifyAll();
        } else {
            System.out.println(Thread.currentThread().getName() + " - [ERROR]虚假唤醒");
        }
    }
}
