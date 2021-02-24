package dev.yxy.lock.clh;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * {@link AtomicReferenceFieldUpdater} 改成了 {@link AtomicReference}
 * Created by Nuclear on 2020/12/21
 */
public class CLHLockV2 {

    private static class Node {
        private volatile boolean isLocked = true;
    }

    private static final AtomicReference<Node> TAIL = new AtomicReference<>();
    private static final ThreadLocal<Node> LOCAL = new ThreadLocal<>();

    public void lock() {
        Node node = new Node();
        LOCAL.set(node);
        Node preNode = TAIL.getAndSet(node);
        if (preNode != null) {
            while (preNode.isLocked) ;
        }
    }

    public void unlock() {
        Node node = LOCAL.get();
        node.isLocked = false;
        // 这种情况只有在最后一个节点上才会发生
        TAIL.compareAndSet(node, null);
    }

    public static void main(String[] args) {
        final CLHLockV2 lock = new CLHLockV2();
        ExecutorService exec = Executors.newFixedThreadPool(3);
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
