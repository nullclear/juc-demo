package dev.yxy.condition.water;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 只适用于三线程
 * Created by atom on 2021-03-09
 */
public class WaterBarrier implements IWater {
    private CyclicBarrier barrier = new CyclicBarrier(3);

    @Override
    public void provideH(Runnable runnable) {
        runnable.run();
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void provideO(Runnable runnable) {
        runnable.run();
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void release() {

    }
}
