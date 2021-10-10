package dev.yxy.atom;

import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * 原子方式更新数组
 * -------------------------------------------------- 小记 --------------------------------------------------
 * 数组只是增加了可操作的数量，并没有改变其他功能
 * Created by yuanxy on 2021/10/09
 */
public class TestAtomicArray {

    public static void main(String[] args) {
        // -------------------------------------------------- AtomicIntegerArray --------------------------------------------------
        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(10);
        atomicIntegerArray.set(0, 1);
        atomicIntegerArray.get(0);
        atomicIntegerArray.compareAndSet(0, 1, 10);

        // -------------------------------------------------- AtomicLongArray --------------------------------------------------
        AtomicLongArray atomicLongArray = new AtomicLongArray(10);
        atomicLongArray.set(0, 1L);
        atomicLongArray.get(0);
        atomicLongArray.compareAndSet(0, 1L, 10L);

        // -------------------------------------------------- AtomicReferenceArray --------------------------------------------------
        Object oldValue = new Object();
        Object newValue = new Object();

        AtomicReferenceArray<Object> atomicReferenceArray = new AtomicReferenceArray<>(10);
        atomicReferenceArray.set(0, oldValue);
        atomicReferenceArray.get(0);
        atomicReferenceArray.compareAndSet(0, oldValue, newValue);
    }
}
