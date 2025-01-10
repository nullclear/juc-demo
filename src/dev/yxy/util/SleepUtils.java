package dev.yxy.util;

import java.util.concurrent.TimeUnit;

/**
 * 线程睡眠工具类
 */
public class SleepUtils {

    public static void second(long timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException ignored) {
        }
    }

    public static void millisecond(long timeout) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException ignored) {
        }
    }
}
