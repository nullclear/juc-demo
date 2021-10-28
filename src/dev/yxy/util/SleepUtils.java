package dev.yxy.util;

import java.util.concurrent.TimeUnit;

/**
 * 线程睡眠工具类
 *
 * @author yuanxy
 * @create 2021/10/28 9:10
 * @update 2021/10/28 9:10
 * @origin juc-demo
 */
public class SleepUtils {

    public static void second(long timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void millisecond(long timeout) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
