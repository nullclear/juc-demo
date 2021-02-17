package dev.yxy.order_example01;

/**
 * Created by atom on 2021/2/16
 */
public class Test {
    public static void main(String[] args) {
        Data data = new Data();
        new Thread(data::printA, "A").start();
        new Thread(data::printB, "B").start();
        new Thread(data::printC, "C").start();
    }
}
