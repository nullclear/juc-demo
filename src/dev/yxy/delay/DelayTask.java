package dev.yxy.delay;

import java.time.Duration;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延时任务
 *
 * @author nullclear
 * @since 2023-02-02
 */
public class DelayTask implements Delayed, Runnable {

    private final Runnable task;

    private final long targetTime;

    public DelayTask(Runnable task, Duration delayTime) {
        this.task = task;
        this.targetTime = System.currentTimeMillis() + delayTime.toMillis();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.targetTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
        if (this == other) {
            return 0;
        }
        if (other instanceof DelayTask) {
            return (int) (this.targetTime - ((DelayTask) other).targetTime);
        }
        return 0;
    }

    @Override
    public void run() {
        this.task.run();
    }
}
