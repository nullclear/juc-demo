package dev.yxy.lock.spin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 自定义可重入自旋锁
 * todo 不公平
 * Created by Nuclear on 2020/12/14
 */
public class ReentrantSpinLock {
    private AtomicReference<Thread> ATOM = new AtomicReference<>();
    private int count;

    public void lock() {
        Thread current = Thread.currentThread();
        // 如果当前线程已经获取到了锁，线程数增加一，然后返回
        if (current == ATOM.get()) {
            count++;
            return;
        }
        // 如果没获取到锁，则通过CAS自旋
        while (!ATOM.compareAndSet(null, current)) {
            // DO nothing
        }
    }

    public void unlock() {
        Thread current = Thread.currentThread();
        if (current == ATOM.get()) {
            // 如果大于0，表示当前线程多次获取了该锁，释放锁通过count减一来模拟
            if (count > 0) {
                count--;
            } else {
                // 如果count == 0，就可以将锁释放，这样就能保证获取锁的次数与释放锁的次数是一致的了。
                ATOM.compareAndSet(current, null);
            }
        }
    }

    public static void main(String[] args) {
        final ReentrantLock lock = new ReentrantLock();
        ExecutorService exec = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 6; i++) {
            final int temp = i;
            exec.submit(() -> {
                lock.lock();//加锁
                lock.lock();//加锁
                System.out.println(temp);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.unlock();//解锁
                lock.unlock();//解锁
            });
        }
        exec.shutdown();
    }
}
