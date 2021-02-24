package dev.yxy.blocking;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * 数组阻塞队列(有界阻塞队列)
 * Created by atom on 2021/2/18
 */
public class TestArrayBlockingQueue {

    public static void main(String[] args) throws InterruptedException {
        //实现了Queue接口，这里只需要了解
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

        //实现了BlockingQueue接口
        ArrayBlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(20);

        //这一组操作不当会报异常
        blockingQueue.add("one");
        blockingQueue.element();
        blockingQueue.remove();

        //这一组操作不当不会报异常
        blockingQueue.offer("two");
        blockingQueue.peek();
        blockingQueue.poll();

        //这一组操作会无限阻塞
        //waiting for space to become available if the queue is full.
        blockingQueue.put("three");
        //waiting if necessary until an element becomes available.
        blockingQueue.take();

        //这一组操作限时等待
        blockingQueue.offer("four", 2, TimeUnit.SECONDS);
        blockingQueue.poll(2, TimeUnit.SECONDS);
    }
}
