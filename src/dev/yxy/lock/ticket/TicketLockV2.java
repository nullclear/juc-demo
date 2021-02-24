package dev.yxy.lock.ticket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多处理器系统上，每个进程/线程占用的处理器都在读写同一个变量serviceNum ，
 * 每次读写操作都必须在多个处理器缓存之间进行缓存同步，
 * 这会导致繁重的系统总线和内存的流量，大大降低系统整体的性能。
 * Created by Nuclear on 2020/12/21
 */
public class TicketLockV2 {
    /**
     * 服务号
     */
    private AtomicInteger serviceNum = new AtomicInteger(1);

    /**
     * 排队号
     */
    private AtomicInteger ticketNum = new AtomicInteger();

    /**
     * 新增一个ThreadLocal，用于存储每个线程的排队号
     */
    private ThreadLocal<Integer> ticketNumHolder = new ThreadLocal<>();

    public void lock() {
        int currentTicketNum = ticketNum.incrementAndGet();
        // 获取锁的时候，将当前线程的排队号保存起来
        ticketNumHolder.set(currentTicketNum);
        // 如果现在的排队号大于服务号，就一直等待
        while (currentTicketNum > serviceNum.get()) {
            // Do nothing
        }
    }

    public void unlock() {
        // 释放锁，从ThreadLocal中获取当前线程的排队号
        Integer currentTicketNum = ticketNumHolder.get();
        serviceNum.compareAndSet(currentTicketNum, currentTicketNum + 1);
    }

    public static void main(String[] args) {
        final TicketLockV2 lock = new TicketLockV2();
        ExecutorService exec = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            final int temp = i;
            exec.submit(() -> {
                lock.lock();//加锁
                System.out.println(temp);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.unlock();//解锁
            });
        }
        exec.shutdown();
    }
}
