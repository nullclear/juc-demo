package dev.yxy.condition.water;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Nuclear on 2021/2/24
 */
public class TestGenerateWater {
    private static final int num = 100;

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 50; i++) {
            //OK
            testTwoThead(new WaterFakeSemaphore());

            //OK
            testThreeThead(new WaterFakeSemaphore());

            //OK
            testTwoThead(new WaterSemaphore());

            //OK
            testThreeThead(new WaterSemaphore());

            //OK
            testThreeThead(new WaterBarrier());
        }
        long diff = System.currentTimeMillis() - start;
        System.out.println("通过测试，耗费时间：" + diff + "ms");
    }

    /**
     * 测试两条线程生成水
     */
    private static void testTwoThead(IWater water) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        StringBuffer buffer = new StringBuffer();

        new Thread(() -> {
            for (int i = 0; i < num; i++) {
                water.provideO(() -> buffer.append("O"));
            }
            water.release();
            latch.countDown();
        }, "O").start();

        new Thread(() -> {
            for (int i = 0; i < (num * 2); i++) {
                water.provideH(() -> buffer.append("H"));
            }
            water.release();
            latch.countDown();
        }, "H").start();

        latch.await();
        check(buffer.toString());
    }

    /**
     * 测试三条线程生成水
     */
    private static void testThreeThead(IWater water) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        StringBuffer buffer = new StringBuffer();

        new Thread(() -> {
            for (int i = 0; i < num; i++) {
                water.provideO(() -> buffer.append("O"));
            }
            water.release();
            latch.countDown();
        }, "O").start();

        new Thread(() -> {
            for (int i = 0; i < num; i++) {
                water.provideH(() -> buffer.append("H"));
            }
            water.release();
            latch.countDown();
        }, "H1").start();

        new Thread(() -> {
            for (int i = 0; i < num; i++) {
                water.provideH(() -> buffer.append("H"));
            }
            water.release();
            latch.countDown();
        }, "H2").start();

        latch.await();
        check(buffer.toString());
    }

    /**
     * 检查结果是否合法
     */
    private static void check(String result) {
        char[] chars = result.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            builder.append(chars[i]);
            if ((i + 1) % 3 == 0) {
                String string = builder.toString();
                boolean flag = string.matches("^HHO|OHH|HOH$");
                if (!flag) {
                    throw new IllegalArgumentException(string);
                }
                builder.delete(0, builder.length());
            }
        }
    }
}
