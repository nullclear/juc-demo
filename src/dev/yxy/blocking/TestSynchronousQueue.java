package dev.yxy.blocking;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * 同步队列。
 * each insert operation must wait for a corresponding remove operation by another thread, and vice versa.
 * ----
 * SynchronousQueue不存储元素，必须有另外一个线程执行获取元素之后，才会结束阻塞状态。
 * 例子中的第一个put语句会阻塞3秒，因为3秒后才有第一个take语句出现。
 * Created by atom on 2021/2/18
 */
public class TestSynchronousQueue {

    public static void main(String[] args) {
        SynchronousQueue<String> synchronousQueue = new SynchronousQueue<>();
        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + " 准备 PUT");
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
                System.out.println(Thread.currentThread().getName() + " 准备 TAKE");
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
