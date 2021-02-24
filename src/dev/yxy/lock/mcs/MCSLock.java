package dev.yxy.lock.mcs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * MCSLock是显示链表，有一个指向后续节点的属性。
 * 将获取锁的线程状态借助节点(node)保存,每个线程都有一份独立的节点。
 * MCS适用于NUMA结构的处理器，CLH适用于SMP结构的处理器
 * Created by Nuclear on 2020/12/21
 */
public class MCSLock {

    /**
     * 节点，记录当前节点的锁状态以及后驱节点
     */
    private static class Node {
        private volatile Node next;
        private volatile boolean isLocked = true;
    }

    private static final ThreadLocal<Node> LOCAl = new ThreadLocal<>();
    // 队列
    private volatile Node queue;
    // queue更新器
    private static final AtomicReferenceFieldUpdater<MCSLock, Node> UPDATER = AtomicReferenceFieldUpdater.newUpdater(MCSLock.class, Node.class, "queue");

    public void lock() {
        // 创建节点并保存到ThreadLocal中
        Node currentNode = new Node();
        LOCAl.set(currentNode);
        // 将queue设置为当前节点，并且返回之前的节点
        Node preNode = UPDATER.getAndSet(this, currentNode);
        if (preNode != null) {
            // 如果之前节点不为null，表示锁已经被其他线程持有
            preNode.next = currentNode;
            // 循环判断，直到当前节点的锁标志位为false
            while (currentNode.isLocked) ;
        }
    }

    public void unlock() {
        Node currentNode = LOCAl.get();
        // next为null表示没有正在等待获取锁的线程
        if (currentNode.next == null) {
            // 更新状态并设置queue为null
            if (UPDATER.compareAndSet(this, currentNode, null)) {
                // 如果成功了，表示queue==currentNode，即当前节点后面没有节点了
            } else {
                // 如果不成功，表示queue!=currentNode，即当前节点后面多了一个节点，表示有线程在进行CAS操作
                // 如果当前节点的后续节点为null，则需要等待其不为null（参考加锁方法）
                while (currentNode.next == null) ;
                // 将此节点解锁
                currentNode.next.isLocked = false;
                currentNode.next = null;
            }
        } else {
            // 如果不为null，表示有线程在等待获取锁，此时将等待线程对应的节点锁状态更新为false，同时将当前线程的后继节点设为null
            currentNode.next.isLocked = false;
            currentNode.next = null;
        }
    }

    public static void main(String[] args) {
        final MCSLock lock = new MCSLock();
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
