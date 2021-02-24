package dev.yxy.lock.ticket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 排队锁解决公平性问题
 * 这样是有风险的，因为这个排队号是可以被修改的，一旦排队号被不小心修改了，那么锁将不能被正确释放。
 * Created by Nuclear on 2020/12/21
 */
public class TicketLock {
    /**
     * 服务号
     */
    private AtomicInteger serviceNum = new AtomicInteger(1);

    /**
     * 排队号
     */
    private AtomicInteger ticketNum = new AtomicInteger();

    /**
     * lock:获取锁，如果获取成功，返回当前线程的排队号，获取排队号用于释放锁.
     *
     * @return 排队号
     */
    public int lock() {
        int currentTicketNum = ticketNum.incrementAndGet();
        //如果现在的排队号大于服务号，就一直等待
        while (currentTicketNum > serviceNum.get()) {
            // Do nothing
        }
        return currentTicketNum;
    }

    /**
     * unlock:释放锁，传入当前持有锁的线程的排队号
     *
     * @param ticketNum 排队号
     */
    public void unlock(int ticketNum) {
        serviceNum.compareAndSet(ticketNum, ticketNum + 1);
    }

    public static void main(String[] args) {
        final TicketLock lock = new TicketLock();
        ExecutorService exec = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            final int temp = i;
            exec.submit(() -> {
                int num = lock.lock();//加锁
                System.out.println(temp);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.unlock(num);//解锁
            });
        }
        exec.shutdown();
    }
}
