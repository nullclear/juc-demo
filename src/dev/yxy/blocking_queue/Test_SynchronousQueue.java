package dev.yxy.blocking_queue;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * 同步队列，模糊点来说，可以看作容量1的队列，但是实际上有很大的不同
 * SynchronousQueue不存储元素，必须有另外一个线程获取元素之后，才会继续执行下一句
 * 例子中的第一个put语句会阻塞3秒才执行下一个语句
 * Created by atom on 2021/2/18
 */
public class Test_SynchronousQueue {
    public static void main(String[] args) {
        SynchronousQueue<String> synchronousQueue = new SynchronousQueue<>();
        new Thread(() -> {
            try {
                synchronousQueue.put("1");
                System.out.println(Thread.currentThread().getName() + " PUT 1");

                synchronousQueue.put("2");
                System.out.println(Thread.currentThread().getName() + " PUT 2");

                synchronousQueue.put("3");
                System.out.println(Thread.currentThread().getName() + " PUT 3");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "A").start();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
                String s1 = synchronousQueue.take();
                System.out.println(Thread.currentThread().getName() + " TAKE " + s1);

                TimeUnit.SECONDS.sleep(2);
                String s2 = synchronousQueue.take();
                System.out.println(Thread.currentThread().getName() + " TAKE " + s2);

                TimeUnit.SECONDS.sleep(2);
                String s3 = synchronousQueue.take();
                System.out.println(Thread.currentThread().getName() + " TAKE " + s3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "B").start();
    }
}
