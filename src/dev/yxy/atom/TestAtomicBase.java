package dev.yxy.atom;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 原子方式更新基本类型
 * -------------------------------------------------- 小记 --------------------------------------------------
 * 基本类型主要用于实现“引用类型”与“CAS”的功能
 * Created by yuanxy on 2021/10/09
 */
public class TestAtomicBase {

    public static void main(String[] args) {
        // -------------------------------------------------- AtomicBoolean --------------------------------------------------
        AtomicBoolean atomicBoolean = new AtomicBoolean(Boolean.FALSE);
        atomicBoolean.get();
        atomicBoolean.set(Boolean.FALSE);
        atomicBoolean.compareAndSet(Boolean.FALSE, Boolean.TRUE);

        // -------------------------------------------------- AtomicInteger --------------------------------------------------
        AtomicInteger atomicInteger = new AtomicInteger(0);
        atomicInteger.get();
        atomicInteger.set(0);
        atomicInteger.compareAndSet(0, 1);

        // -------------------------------------------------- AtomicLong --------------------------------------------------
        AtomicLong atomicLong = new AtomicLong(0L);
        atomicLong.get();
        atomicLong.set(0L);
        atomicLong.compareAndSet(0L, 1L);
    }
}
