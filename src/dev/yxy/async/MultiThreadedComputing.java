package dev.yxy.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 使用FutureTask进行多线程计算
 * Created by Nuclear on 2020/8/11
 */
public class MultiThreadedComputing {

    public static void main(String[] args) throws InterruptedException {
        // 创建任务集合
        List<FutureTask<Integer>> taskList = new ArrayList<>();
        // 创建线程池，这里10个任务要分两批才能计算完成，可以看console的停顿
        ExecutorService exec = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            // 传入Callable对象创建FutureTask对象
            FutureTask<Integer> task = new FutureTask<>(new ComputeTask(i, "" + i));
            taskList.add(task);
            // 提交给线程池执行任务
            exec.submit(task);
        }

        //模拟主线程的其他任务
        System.out.println("所有计算任务提交完毕, [主线程]接着干其他事情！");
        TimeUnit.SECONDS.sleep(2);
        System.out.println("[主线程]完成任务，开始统计各计算线程计算结果！");

        // 开始统计各计算线程计算结果
        int totalResult = 0;
        for (FutureTask<Integer> item : taskList) {
            try {
                //FutureTask的get方法会自动阻塞,直到获取计算结果为止
                totalResult = totalResult + item.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // 关闭线程池
        exec.shutdown();
        System.out.println("多任务计算后的总结果是:" + totalResult);
    }

    private static class ComputeTask implements Callable<Integer> {

        private Integer result;
        private String taskName;

        public ComputeTask(Integer seed, String taskName) {
            result = seed;
            this.taskName = taskName;
            System.out.println("生成子线程计算任务: " + taskName);
        }

        @Override
        public Integer call() throws Exception {
            for (int i = 0; i < 100; i++) {
                result += i;
            }
            // 休眠5秒钟，观察主线程行为，预期的结果是主线程会继续执行，到要取得FutureTask的结果是等待直至完成。
            TimeUnit.SECONDS.sleep(5);
            System.out.println("子线程计算任务: " + taskName + " 执行完成! 结果:" + result);
            return result;
        }
    }
}
