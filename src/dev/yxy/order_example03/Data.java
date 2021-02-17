package dev.yxy.order_example03;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by atom on 2021/2/16
 */
public class Data {
    private int num = 0;
    private ReentrantLock lock = new ReentrantLock();
    private Condition conditionA = lock.newCondition();
    private Condition conditionB = lock.newCondition();
    private Condition conditionC = lock.newCondition();

    public void printA() {
        for (int i = 0; i < 10; i++) {
            lock.lock();
            try {
                while (num != 0) {
                    conditionA.await();
                }
                System.out.print("a");
                num = 1;
                conditionB.signal();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    public void printB() {
        for (int i = 0; i < 10; i++) {
            lock.lock();
            try {
                while (num != 1) {
                    conditionB.await();
                }
                System.out.print("b");
                num = 2;
                conditionC.signal();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    public void printC() {
        for (int i = 0; i < 10; i++) {
            lock.lock();
            try {
                while (num != 2) {
                    conditionC.await();
                }
                System.out.println("c");
                num = 0;
                conditionA.signal();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
}
