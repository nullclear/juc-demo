package dev.yxy.order;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 原子变量+自旋控制顺序
 * Created by atom on 2021/2/24
 */
public class DataAtom implements IData {
    private AtomicInteger num = new AtomicInteger(0);

    @Override
    public void printA(Runnable runnable) {
        for (int i = 0; i < 10; i++) {
            while (num.get() != 0) ;
            runnable.run();
            num.set(1);
        }
    }

    @Override
    public void printB(Runnable runnable) {
        for (int i = 0; i < 10; i++) {
            while (num.get() != 1) ;
            runnable.run();
            num.set(2);
        }
    }

    @Override
    public void printC(Runnable runnable) {
        for (int i = 0; i < 10; i++) {
            while (num.get() != 2) ;
            runnable.run();
            num.set(0);
        }
    }
}
