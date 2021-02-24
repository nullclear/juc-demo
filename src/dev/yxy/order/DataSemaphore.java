package dev.yxy.order;

import java.util.concurrent.Semaphore;

/**
 * 信号量控制顺序
 * Created by atom on 2021/2/24
 */
public class DataSemaphore implements IData {
    private Semaphore a = new Semaphore(1);
    private Semaphore b = new Semaphore(0);
    private Semaphore c = new Semaphore(0);

    @Override
    public void printA(Runnable runnable) {
        for (int i = 0; i < 10; i++) {
            try {
                a.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runnable.run();
            b.release();
        }
    }

    @Override
    public void printB(Runnable runnable) {
        for (int i = 0; i < 10; i++) {
            try {
                b.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runnable.run();
            c.release();
        }
    }

    @Override
    public void printC(Runnable runnable) {
        for (int i = 0; i < 10; i++) {
            try {
                c.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runnable.run();
            a.release();
        }
    }
}
