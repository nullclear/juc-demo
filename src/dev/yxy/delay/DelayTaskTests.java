package dev.yxy.delay;

import dev.yxy.util.SleepUtils;
import org.junit.Test;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 延时任务单元测试
 * <p>
 * DelayQueue原理及使用: https://zhuanlan.zhihu.com/p/443335543
 * DelayQueue搞定超时订单: https://juejin.cn/post/6844903955160383502
 * DelayQueue延迟队列和Redis缓存实现订单自动取消功能: https://blog.csdn.net/cstp321/article/details/124730874
 *
 * @author nullclear
 * @since 2023-02-02
 */
public abstract class DelayTaskTests {

    /**
     * 通过多个随机延迟任务来测试延迟队列的调度
     */
    public static class MultiRandomTask {

        private final long baseTime = System.currentTimeMillis();
        private final int totalSize = 100;
        private final int baseDelayTime = 50;
        private final int bound = 10000;
        private final Random random = new Random();

        private final DelayTaskManager manager = new DelayTaskManager(3, Thread::new);

        @Test
        public void test() {
            testStart();

            prepareTask();
            for (int i = 1; i <= totalSize; i++) {
                int taskNo = i;
                long delayTime = baseDelayTime + random.nextInt(bound);
                long targetTime = System.currentTimeMillis() + delayTime;
                manager.submit(() -> {
                    long currentTime = System.currentTimeMillis();
                    System.out.printf("ThreadName:[%s] TaskNo:[%03d] DelayTime:[%s] CurrentTime:[%s] TargetTime:[%s] Diff:[%s]\n",
                            Thread.currentThread().getName(), taskNo, delayTime, (currentTime - baseTime), (targetTime - baseTime), (currentTime - targetTime));
                }, Duration.ofMillis(delayTime));
            }
            submitAllTask();

            SleepUtils.millisecond(bound);

            prepareShutdown();
            manager.shutdown();
            shutdownSuccess();

            SleepUtils.second(2);

            testEnd();
        }

        private void testStart() {
            System.out.printf("ThreadName:[%s] CurrentTime:[%s] test start!\n",
                    Thread.currentThread().getName(), (System.currentTimeMillis() - baseTime));
        }

        private void prepareTask() {
            System.out.printf("ThreadName:[%s] CurrentTime:[%s] TaskSize:[%s] Bound:[%s, %s) prepare...\n",
                    Thread.currentThread().getName(), (System.currentTimeMillis() - baseTime), totalSize, baseDelayTime, (baseDelayTime + bound));
        }

        private void submitAllTask() {
            System.out.printf("ThreadName:[%s] CurrentTime:[%s] TaskSize:[%s] Bound:[%s, %s) all submit! TaskCount:[%s]\n",
                    Thread.currentThread().getName(), (System.currentTimeMillis() - baseTime), totalSize, baseDelayTime, (baseDelayTime + bound), manager.getTaskCount());
        }

        private void prepareShutdown() {
            System.out.printf("ThreadName:[%s] CurrentTime:[%s] shutdown prepare...\n",
                    Thread.currentThread().getName(), (System.currentTimeMillis() - baseTime));
        }

        private void shutdownSuccess() {
            System.out.printf("ThreadName:[%s] CurrentTime:[%s] shutdown success! CompletedTaskCount:[%s]\n",
                    Thread.currentThread().getName(), (System.currentTimeMillis() - baseTime), manager.getCompletedTaskCount());
        }

        private void testEnd() {
            System.out.printf("ThreadName:[%s] CurrentTime:[%s] test end!\n",
                    Thread.currentThread().getName(), (System.currentTimeMillis() - baseTime));
        }
    }

    /**
     * 虚假的延迟任务执行器
     */
    public static class FakeDelayExecutor {

        private final long baseTime = System.currentTimeMillis();

        private final long delayTime = 500;

        @Test
        public void test() {
            testStart();

            //noinspection unchecked
            ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new DelayQueue());

            executor.execute(() -> {
                System.out.println("线程池默认是懒加载启动线程，且此线程的第一个任务不阻塞，所以必须先预热下线程池，才能实现DelayQueue的延迟特性！");
            });

            long targetTime = System.currentTimeMillis() + delayTime;
            executor.execute(new DelayTask(() -> {
                long currentTime = System.currentTimeMillis();
                System.out.printf("ThreadName:[%s] DelayTime:[%s] CurrentTime:[%s] TargetTime:[%s] Diff:[%s]\n",
                        Thread.currentThread().getName(), delayTime, (currentTime - baseTime), (targetTime - baseTime), (currentTime - targetTime));
            }, Duration.ofMillis(500)));

            SleepUtils.second(2);

            testEnd();
        }

        private void testStart() {
            System.out.printf("ThreadName:[%s] CurrentTime:[%s] test start!\n",
                    Thread.currentThread().getName(), (System.currentTimeMillis() - baseTime));
        }

        private void testEnd() {
            System.out.printf("ThreadName:[%s] CurrentTime:[%s] test end!\n",
                    Thread.currentThread().getName(), (System.currentTimeMillis() - baseTime));
        }
    }

    /**
     * 根据判断条件自动调整的延迟任务
     */
    public static class SelfCycleDelayTask {

        private final long baseTime = System.currentTimeMillis();

        private final long delayTime = 500;

        private final long targetTime = baseTime + 5000;

        private final DelayTaskManager manager = new DelayTaskManager(3, Thread::new);

        @Test
        public void test() {
            testStart();

            manager.submit(new Runnable() {
                @Override
                public void run() {
                    long currentTime = System.currentTimeMillis();
                    System.out.printf("ThreadName:[%s] DelayTime:[%s] CurrentTime:[%s] TargetTime:[%s] Diff:[%s]\n",
                            Thread.currentThread().getName(), delayTime, (currentTime - baseTime), (targetTime - baseTime), (currentTime - targetTime));
                    if (currentTime < targetTime) {
                        manager.submit(this, Duration.ofMillis(delayTime));

                        // 小睡一会，把任务让给其他线程调度
                        SleepUtils.millisecond(10);
                    }
                }
            }, Duration.ofMillis(delayTime));

            SleepUtils.second(6);

            testEnd();
        }

        private void testStart() {
            System.out.printf("ThreadName:[%s] CurrentTime:[%s] test start!\n",
                    Thread.currentThread().getName(), (System.currentTimeMillis() - baseTime));
        }

        private void testEnd() {
            System.out.printf("ThreadName:[%s] CurrentTime:[%s] test end!\n",
                    Thread.currentThread().getName(), (System.currentTimeMillis() - baseTime));
        }
    }
}
