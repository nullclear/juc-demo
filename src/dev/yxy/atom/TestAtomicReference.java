package dev.yxy.atom;

import dev.yxy.lock.clh.CLHLock;

import java.util.concurrent.atomic.*;

/**
 * 原子方式更新引用
 * -------------------------------------------------- 小记 --------------------------------------------------
 * AtomicReferenceFieldUpdater 用法可以参考 {@link CLHLock}
 * Created by yuanxy on 2021/10/09
 */
public class TestAtomicReference {

    public static void main(String[] args) {
        // -------------------------------------------------- AtomicReference --------------------------------------------------
        {
            User oldUser = new User("hit", 15);
            User newUser = new User("lee", 17);

            AtomicReference<User> atomicReference = new AtomicReference<>();
            atomicReference.set(oldUser);
            atomicReference.get();
            atomicReference.compareAndSet(oldUser, newUser);
        }

        // -------------------------------------------------- AtomicReferenceFieldUpdater --------------------------------------------------
        {
            User obj = new User("hit", 15);

            AtomicReferenceFieldUpdater<User, Integer> atomicReferenceFieldUpdater = AtomicReferenceFieldUpdater.newUpdater(User.class, Integer.class, "age");
            // 这里get的结果是null
            atomicReferenceFieldUpdater.getAndSet(obj, 20);
            atomicReferenceFieldUpdater.compareAndSet(obj, 20, 28);
        }

        // -------------------------------------------------- AtomicIntegerFieldUpdater --------------------------------------------------
        {
            User obj = new User("hit", 15);

            //noinspection AtomicFieldUpdaterIssues
            AtomicIntegerFieldUpdater<User> atomicIntegerFieldUpdater = AtomicIntegerFieldUpdater.newUpdater(User.class, "age");
            atomicIntegerFieldUpdater.getAndSet(obj, 20);
            atomicIntegerFieldUpdater.compareAndSet(obj, 20, 28);
        }

        // -------------------------------------------------- AtomicLongFieldUpdater --------------------------------------------------

        {
            // 与AtomicIntegerFieldUpdater只是类型区别
        }

        // -------------------------------------------------- 标记可以解决ABA问题 --------------------------------------------------

        // -------------------------------------------------- AtomicMarkableReference --------------------------------------------------
        {
            AtomicMarkableReference<Integer> atomicMarkableReference = new AtomicMarkableReference<>(0, Boolean.FALSE);
            atomicMarkableReference.attemptMark(0, Boolean.TRUE);
            atomicMarkableReference.compareAndSet(0, 1, Boolean.TRUE, Boolean.FALSE);
        }

        // -------------------------------------------------- AtomicStampedReference --------------------------------------------------
        {
            AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<>(0, 0);
            atomicStampedReference.compareAndSet(0, 10, 0, 1);
        }
    }
}
