package dev.yxy.atom;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 乐观锁，带版本号可以解决ABA问题
 * Created by Nuclear on 2021/2/19
 */
public class TestAtomicStampedReference {

    private static final AtomicStampedReference<Integer> reference = new AtomicStampedReference<>(1, 1);

    public static void main(String[] args) {
        new Thread(() -> {
            int stamp = reference.getStamp(); // 获得版本号
            System.out.println("a1 => " + stamp);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("[a] 1 改成 2 => " + reference.compareAndSet(1, 2, stamp, stamp + 1));
            System.out.println("a2 => " + (stamp = reference.getStamp()));

            System.out.println("[a] 2 改成 1 => " + reference.compareAndSet(2, 1, stamp, stamp + 1));
            System.out.println("a3 => " + reference.getStamp());
        }, "a").start();

        new Thread(() -> {
            int stamp = reference.getStamp(); // 获得版本号
            System.out.println("b1 => " + stamp);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("[b] 1 改成 6 => " + reference.compareAndSet(1, 6, stamp, stamp + 1));
            System.out.println("b2 => " + stamp + " now stamp => " + reference.getStamp());
        }, "b").start();
    }
}
