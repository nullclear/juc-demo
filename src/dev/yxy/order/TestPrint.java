package dev.yxy.order;

import java.util.concurrent.CountDownLatch;

/**
 * Created by atom on 2021/2/24
 */
public class TestPrint {

    public static void main(String[] args) throws InterruptedException {
        print(new DataAtom());
        print(new DataSemaphore());
        print(new DataCondition());
        print(new DataSynchronized());
    }

    private static void print(IData data) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);

        new Thread(() -> {
            data.printA(() -> System.out.print("A"));
            latch.countDown();
        }, "A").start();

        new Thread(() -> {
            data.printB(() -> System.out.print("B"));
            latch.countDown();
        }, "B").start();

        new Thread(() -> {
            data.printC(() -> System.out.print("C   "));
            latch.countDown();
        }, "C").start();

        latch.await();
        System.out.println();
    }

}
