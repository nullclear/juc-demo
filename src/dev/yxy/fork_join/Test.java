package dev.yxy.fork_join;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

/**
 * ForkJoin并行执行任务
 * Created by Nuclear on 2021/2/19
 */
public class Test {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        test00();
        test01();
        test02();
        test03();
        //test00 sum=500000000500000000 时间：0ms
        //test01 sum=500000000500000000 时间：448ms
        //test02 sum=500000000500000000 时间：4330ms
        //test03 sum=500000000500000000 时间：183ms
    }

    public static class ForkJoinExample extends RecursiveTask<Long> {
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

    private static void test00() {
        long start = System.currentTimeMillis();
        long n = 10_0000_0000L;
        long sum = ((1 + n) * n) >>> 1;
        long end = System.currentTimeMillis();
        System.out.println("test00 sum=" + sum + " 时间：" + (end - start) + "ms");
    }

    private static void test01() {
        long start = System.currentTimeMillis();
        long sum = 0L;
        for (long i = 1L; i <= 10_0000_0000L; i++) sum += i;
        long end = System.currentTimeMillis();
        System.out.println("test01 sum=" + sum + " 时间：" + (end - start) + "ms");
    }

    private static void test02() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinExample task = new ForkJoinExample(0L, 10_0000_0000L);
        ForkJoinTask<Long> submit = pool.submit(task);
        Long sum = submit.get();
        long end = System.currentTimeMillis();
        System.out.println("test02 sum=" + sum + " 时间：" + (end - start) + "ms");
    }

    private static void test03() {
        long start = System.currentTimeMillis();
        long sum = LongStream.rangeClosed(0L, 10_0000_0000L).parallel().reduce(0, Long::sum);
        long end = System.currentTimeMillis();
        System.out.println("test03 sum=" + sum + " 时间：" + (end - start) + "ms");
    }
}