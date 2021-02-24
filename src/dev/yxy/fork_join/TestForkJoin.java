package dev.yxy.fork_join;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.LongStream;

/**
 * ForkJoin并行执行任务
 * Created by Nuclear on 2021/2/19
 */
public class TestForkJoin {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        testFormula();
        testViolence();
        testBranchMerge();
        testStreamReduce();
        //testFormula sum=500000000500000000 时间：0ms
        //testViolence sum=500000000500000000 时间：313ms
        //testBranchMerge sum=500000000500000000 时间：1816ms
        //testStreamReduce sum=500000000500000000 时间：83ms
    }

    private static void testFormula() {
        long start = System.currentTimeMillis();
        long n = 10_0000_0000L;
        long sum = ((1 + n) * n) >>> 1;
        long end = System.currentTimeMillis();
        System.out.println("testFormula sum=" + sum + " 时间：" + (end - start) + "ms");
    }

    private static void testViolence() {
        long start = System.currentTimeMillis();
        long sum = 0L;
        for (long i = 1L; i <= 10_0000_0000L; i++) sum += i;
        long end = System.currentTimeMillis();
        System.out.println("testViolence sum=" + sum + " 时间：" + (end - start) + "ms");
    }

    private static void testBranchMerge() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinExample task = new ForkJoinExample(0L, 10_0000_0000L);
        ForkJoinTask<Long> submit = pool.submit(task);
        Long sum = submit.get();
        long end = System.currentTimeMillis();
        System.out.println("testBranchMerge sum=" + sum + " 时间：" + (end - start) + "ms");
    }

    private static void testStreamReduce() {
        long start = System.currentTimeMillis();
        long sum = LongStream.rangeClosed(0L, 10_0000_0000L).parallel().reduce(0, Long::sum);
        long end = System.currentTimeMillis();
        System.out.println("testStreamReduce sum=" + sum + " 时间：" + (end - start) + "ms");
    }
}