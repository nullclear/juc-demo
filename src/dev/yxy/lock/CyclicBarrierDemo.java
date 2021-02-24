package dev.yxy.lock;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Condition;

/**
 * 司令下达命令，要求10个士兵一起去完成一项任务。
 * 这时，就会要求10个士兵先集合报道，接着，一起雄赳赳气昂昂地执行任务。
 * 当10个士兵把自己手头的任务都执行完成了，那么司令才能对外宣布，任务完成！
 * ----
 * 类似于加法计数器，源码没细看，但是肯定和 {@link Condition#await()} 有关系
 * 多用于多个线程间互相等待，达到条件后再执行指定任务
 * 同时 {@link CyclicBarrier} 可以重置，而 {@link CountDownLatch} 就没有重置功能
 * Created by Nuclear on 2020/12/18
 */
public class CyclicBarrierDemo {

    private static class Soldier implements Runnable {

        private String name;
        private CyclicBarrier cyclicBarrier;

        public Soldier(String name, CyclicBarrier cyclicBarrier) {
            this.name = name;
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void run() {
            try {
                System.out.println(name + " 来报道...");
                //等待所有士兵集合完成
                cyclicBarrier.await();
                doWork();
                //等待所有士兵工作完成
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }

        void doWork() throws InterruptedException {
            //完成工作需要时间
            Thread.sleep(new Random().nextInt(10) * 1000);
            System.out.println(name + " 任务已经完成...");
        }
    }

    private static class doOrder implements Runnable {
        boolean flag = false;
        int n;

        public doOrder(int n) {
            this.n = n;
        }

        @Override
        public void run() {
            if (flag) {
                System.out.println("司令: 士兵" + n + "个任务完成");
            } else {
                System.out.println("司令: 士兵" + n + "个集合完毕");
                flag = true;
            }
        }
    }

    public static void main(String[] args) {
        final int n = 10;

        Thread[] soldiers = new Thread[n];
        // 等待所有线程到达后就会执行doOrder
        CyclicBarrier barrier = new CyclicBarrier(n, new doOrder(n));

        for (int i = 0; i < 10; i++) {
            soldiers[i] = new Thread(new Soldier("士兵-" + i, barrier));
            soldiers[i].start();
        }
    }
}
