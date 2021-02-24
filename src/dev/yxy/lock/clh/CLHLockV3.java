package dev.yxy.lock.clh;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 复用 {@link Node}，避免每次初始化
 * Created by Nuclear on 2020/12/21
 */
public class CLHLockV3 {

    private static class Node {
        private volatile boolean isLocked;
    }

    private static final AtomicReference<Node> TAIL = new AtomicReference<>();
    // 每个线程都会初始化一个，注意与V2版本不同之处在于，这个node会复用，所以要重置isLocked
    private static final ThreadLocal<Node> LOCAL = ThreadLocal.withInitial(Node::new);

    public void lock() {
        // 获取当前线程持有的node
        Node node = LOCAL.get();
        // 重置isLocked
        node.isLocked = true;
        // 获取当前的尾节点，即当前节点的前置节点
        Node preNode = TAIL.getAndSet(node);
        // 如果存在前置节点
        if (preNode != null) {
            // 根据前驱节点状态进行自旋
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
        final CLHLockV3 lock = new CLHLockV3();
        ExecutorService exec = Executors.newFixedThreadPool(4);
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
