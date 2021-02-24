package dev.yxy.condition.water;

import java.util.concurrent.Semaphore;

/**
 * 信号量控制顺序
 * Created by Nuclear on 2021/2/24
 */
public class WaterSemaphore implements IWater {
    private Semaphore h = new Semaphore(2);
    private Semaphore o = new Semaphore(0);

    @Override
    public void provideH(Runnable runnable) {
        try {
            h.acquire();
            runnable.run();
            o.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void provideO(Runnable runnable) {
        try {
            o.acquire(2);
            runnable.run();
            h.release(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void release() {

    }
}
