package dev.yxy.atom;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 原子方式更新基本类型
 * Created by yuanxy on 2021/10/09
 */
public class TestAtomicBase {

    public static void main(String[] args) {
        // -------------------------------------------------- AtomicBoolean --------------------------------------------------
        AtomicBoolean atomicBoolean = new AtomicBoolean(Boolean.FALSE);
        atomicBoolean.compareAndSet(Boolean.FALSE, Boolean.TRUE);

        // -------------------------------------------------- AtomicInteger --------------------------------------------------
        AtomicInteger atomicInteger = new AtomicInteger(0);
        atomicInteger.compareAndSet(0, 1);

        // -------------------------------------------------- AtomicLong --------------------------------------------------
        AtomicLong atomicLong = new AtomicLong(0L);
        atomicLong.compareAndSet(0L, 1L);
    }
}
