package dev.yxy.atom;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * 原子方式更新引用
 * Created by yuanxy on 2021/10/09
 */
public class TestAtomicReference {

    public static void main(String[] args) {
        // -------------------------------------------------- AtomicReference --------------------------------------------------
        AtomicReference<User> atomicReference = new AtomicReference<>();
        User user = new User("hit", 15);
        atomicReference.set(user);
        User updateUser = new User("lee", 17);
        atomicReference.compareAndSet(user, updateUser);

        // -------------------------------------------------- AtomicReferenceFieldUpdater --------------------------------------------------
        AtomicReferenceFieldUpdater<User, Integer> atomicReferenceFieldUpdater = AtomicReferenceFieldUpdater.newUpdater(User.class, Integer.class, "age");
        User atomicUser = new User("hit", 15);
        atomicReferenceFieldUpdater.set(atomicUser, 22);
    }
}
