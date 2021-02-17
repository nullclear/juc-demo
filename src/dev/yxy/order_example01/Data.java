package dev.yxy.order_example01;

import java.util.concurrent.Semaphore;

/**
 * Created by atom on 2021/2/16
 */
public class Data {
    private Semaphore a = new Semaphore(1);
    private Semaphore b = new Semaphore(0);
    private Semaphore c = new Semaphore(0);

    public void printA() {
        for (int i = 0; i < 10; i++) {
            try {
                a.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.print("a");
            b.release();
        }
    }

    public void printB() {
        for (int i = 0; i < 10; i++) {
            try {
                b.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.print("b");
            c.release();
        }
    }

    public void printC() {
        for (int i = 0; i < 10; i++) {
            try {
                c.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("c");
            a.release();
        }
    }
}
