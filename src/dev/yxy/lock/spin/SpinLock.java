package dev.yxy.lock.spin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 自定义自旋锁
 * todo 不可重入
 * todo 不公平
 * 1. 如果某个线程持有锁的时间过长，就会导致其它等待获取锁的线程进入循环等待，消耗CPU。使用不当会造成CPU使用率极高。
 * 2. 上面Java实现的自旋锁不是公平的，即无法满足等待时间最长的线程优先获取锁。不公平的锁就会存在“线程饥饿”问题。
 * Created by Nuclear on 2020/12/21
 */
public class SpinLock {
    private AtomicReference<Thread> ATOM = new AtomicReference<>();

    /**
     * 自旋锁，第一个线程cas成功，返回true，不进入循环
     * 其他线程全部返回false，进入自旋状态，并且不断尝试获取锁
     */
    public void lock() {
        Thread current = Thread.currentThread();
        // 利用CAS
        while (!ATOM.compareAndSet(null, current)) {
            // DO nothing
        }
    }

    /**
     * 解锁
     */
    public void unlock() {
        Thread current = Thread.currentThread();
        ATOM.compareAndSet(current, null);
    }

    public static void main(String[] args) {
        final SpinLock spin = new SpinLock();
        ExecutorService exec = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 6; i++) {
            final int temp = i;
            exec.submit(() -> {
                spin.lock();//加锁
                System.out.println(temp);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                spin.unlock();//解锁
            });
        }
        exec.shutdown();
    }
}
