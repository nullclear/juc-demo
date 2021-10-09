package dev.yxy.atom;

import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * 原子方式更新数组
 * Created by yuanxy on 2021/10/09
 */
public class TestAtomicArray {

    public static void main(String[] args) {
        // -------------------------------------------------- AtomicIntegerArray --------------------------------------------------
        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(10);
        atomicIntegerArray.set(0, 1);

        // -------------------------------------------------- AtomicLongArray --------------------------------------------------
        AtomicLongArray atomicLongArray = new AtomicLongArray(10);
        atomicLongArray.set(0, 1L);

        // -------------------------------------------------- AtomicReferenceArray --------------------------------------------------
        AtomicReferenceArray<Object> atomicReferenceArray = new AtomicReferenceArray<>(10);
        atomicReferenceArray.set(0, new Object());
    }
}
