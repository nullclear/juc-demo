package dev.yxy.buffer_synchronized;

/**
 * 管程法模拟缓冲区
 * Created by Nuclear on 2020/12/29
 */
public class zest {
    static CakeBuffer cakeBuffer = new CakeBuffer();

    public static void main(String[] args) {
        new Thread(new Producer()).start();
        new Thread(new Consumer(), "A").start();
        new Thread(new Consumer(), "B").start();
        new Thread(new Consumer(), "C").start();
        new Thread(new Consumer(), "D").start();
    }

    static class Producer implements Runnable {
        @Override
        public void run() {
            //厨师姗姗来迟
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 1000; i++) {
                cakeBuffer.push(new Cake(i));
            }
        }
    }

    static class Consumer implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 250; i++) {
                cakeBuffer.pop();
            }
        }
    }
}
