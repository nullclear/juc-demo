package dev.yxy.lock.clh;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * CLH锁是一种基于链表的可扩展、高性能、公平的自旋锁，申请线程只在本地变量上自旋，它不断轮询前驱节点的状态，如果发现前驱节点释放了锁就结束自旋，获得锁。
 * Created by Nuclear on 2020/12/21
 */
public class CLHLock {
    
    /**
     * 定义一个节点，默认的lock状态为true
     */
    private static class Node {
        private volatile boolean isLocked = true;
    }

    /**
     * 尾部节点,只用一个节点即可
     */
    private volatile Node tail;
    private static final ThreadLocal<Node> LOCAL = new ThreadLocal<>();
    private static final AtomicReferenceFieldUpdater<CLHLock, Node> UPDATER = AtomicReferenceFieldUpdater.newUpdater(CLHLock.class, Node.class, "tail");

    public void lock() {
        // 新建节点并将节点与当前线程保存起来
        Node node = new Node();
        LOCAL.set(node);
        // 将新建的节点设置为尾部节点，并返回旧的节点（原子操作），这里旧的节点实际上就是当前节点的前驱节点
        Node preNode = UPDATER.getAndSet(this, node);
        if (preNode != null) {
            // 前驱节点不为null表示当锁被其他线程占用，通过不断轮询判断前驱节点的锁标志位等待前驱节点释放锁
            while (preNode.isLocked) ;
        }
        // 如果不存在前驱节点，表示该锁没有被其他线程占用，则当前线程获得锁
    }

    public void unlock() {
        // 获取当前线程对应的节点
        Node node = LOCAL.get();
        // 将node的lock状态置为false，表示当前线程释放了锁
        node.isLocked = false;
        // 如果tail节点等于node，则将tail节点更新为null
        // 这种情况只有在最后一个节点上才会发生
        UPDATER.compareAndSet(this, node, null);
    }

    public static void main(String[] args) {
        final CLHLock lock = new CLHLock();
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
