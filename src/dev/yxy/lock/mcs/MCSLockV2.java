package dev.yxy.lock.mcs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Nuclear on 2020/12/21
 */
public class MCSLockV2 {

    private static class Node {
        private volatile Node next;
        private volatile boolean isLocked = true;
    }

    private static final AtomicReference<Node> TAIL = new AtomicReference<>();
    private static final ThreadLocal<Node> LOCAL = new ThreadLocal<>();

    public void lock() {
        Node node = new Node();
        LOCAL.set(node);
        Node preNode = TAIL.getAndSet(node);
        if (preNode != null) {
            preNode.next = node;
            while (node.isLocked) ;
        }
    }

    public void unlock() {
        Node node = LOCAL.get();
        if (node.next == null) {
            // 如果设置失败，说明又有新节点来了
            if (!TAIL.compareAndSet(node, null)) {
                // 等待新节点操作完
                while (node.next == null) ;
                // 解锁新节点
                node.next.isLocked = false;
                node.next = null;
            }
        } else {
            node.next.isLocked = false;
            node.next = null;
        }
    }

    public static void main(String[] args) {
        final MCSLockV2 lock = new MCSLockV2();
        ExecutorService exec = Executors.newFixedThreadPool(2);
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
