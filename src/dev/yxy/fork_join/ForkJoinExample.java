package dev.yxy.fork_join;

import java.util.concurrent.RecursiveTask;

/**
 * Created by atom on 2021/2/24
 */
public class ForkJoinExample extends RecursiveTask<Long> {
    private Long start;
    private Long end;

    // 临界值
    private static final Long threshold = 10000L;

    public ForkJoinExample(Long start, Long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        if ((end - start) < threshold) {
            long sum = 0L;
            for (Long i = start; i <= end; i++) sum += i;
            return sum;
        } else { // fork join 递归
            long middle = (start + end) >>> 1; // 中间值
            ForkJoinExample left = new ForkJoinExample(start, middle);
            left.fork(); // 拆分任务，把任务压入线程队列
            ForkJoinExample right = new ForkJoinExample(middle + 1, end);
            right.fork(); // 拆分任务，把任务压入线程队列
            return left.join() + right.join();
        }
    }
}
