package dev.yxy.condition;

import java.util.concurrent.TimeUnit;

/**
 * 测试 {@link #wait()} 对synchronized的影响
 * Created by Nuclear on 2021/2/24
 */
public class TestWait {

    public static void main(String[] args) throws InterruptedException {
        TestWait testWait = new TestWait();
        new Thread(testWait::testCommon).start();
        new Thread(testWait::testCommon).start();
        new Thread(testWait::testCommon).start();
        new Thread(testWait::testCommon).start();
        TimeUnit.SECONDS.sleep(1);
        System.out.println("----------");
        new Thread(testWait::testSp).start();
        new Thread(testWait::testSp).start();
        new Thread(testWait::testSp).start();
        new Thread(testWait::testSp).start();
        TimeUnit.SECONDS.sleep(3);
        testWait.release();
    }

    /**
     * 普通的synchronized方法类似于一个原子操作，整个方法执行完才允许其他线程访问
     */
    public synchronized void testCommon() {
        System.out.println("start");
        System.out.println("finish");
    }

    /**
     * {@link #wait()}操作破坏了整体性，可以看到连续输出了四个"start"
     * 调用{@link #release()}方法后结束四个线程的等待状态
     * 那后面的"finish"就一起输出了吗?
     * 经过执行测试，会发现每隔一秒输出一个"finish"
     * 从逻辑上来说，这也很合理，monitor范围内的一直在掌控中
     */
    public synchronized void testSp() {
        System.out.println("start");
        try {
            this.wait();
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("finish");
    }

    /**
     * 不管是{@link #wait()}还是{@link #notifyAll()}方法，都需要有监视器存在
     * synchronized反编译就是monitor-enter + monitor-exit
     */
    public synchronized void release() {
        this.notifyAll();
    }
}
