package dev.yxy.order;

/**
 * 其实和 {@link DataCondition} 没啥区别，只不过虚假唤醒会导致性能比较差
 * Created by atom on 2021/2/24
 */
public class DataSynchronized implements IData {
    private volatile int num = 0;

    @Override
    public void printA(Runnable runnable) {
        for (int i = 0; i < 10; i++) {
            synchronized (this) {
                while (num != 0) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runnable.run();
                num = 1;
                this.notifyAll();
            }
        }
    }

    @Override
    public void printB(Runnable runnable) {
        for (int i = 0; i < 10; i++) {
            synchronized (this) {
                while (num != 1) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runnable.run();
                num = 2;
                this.notifyAll();
            }
        }
    }

    @Override
    public void printC(Runnable runnable) {
        for (int i = 0; i < 10; i++) {
            synchronized (this) {
                while (num != 2) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runnable.run();
                num = 0;
                this.notifyAll();
            }
        }
    }
}
