package dev.yxy.condition.water;

/**
 * Created by Nuclear on 2021/2/24
 */
public interface IWater {

    void provideH(Runnable runnable);

    void provideO(Runnable runnable);

    void release();
}
