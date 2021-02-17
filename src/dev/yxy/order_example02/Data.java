package dev.yxy.order_example02;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by atom on 2021/2/16
 */
public class Data {
    private AtomicInteger num = new AtomicInteger(0);

    public void printA() {
        for (int i = 0; i < 10; i++) {
            while (num.get() != 0) ;
            System.out.print("a");
            num.set(1);
        }
    }

    public void printB() {
        for (int i = 0; i < 10; i++) {
            while (num.get() != 1) ;
            System.out.print("b");
            num.set(2);
        }
    }

    public void printC() {
        for (int i = 0; i < 10; i++) {
            while (num.get() != 2) ;
            System.out.println("c");
            num.set(0);
        }
    }
}
