package dev.yxy.singleton;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;

/**
 * AtomicReference和FutureTask的单例模式
 * Created by Nuclear on 2021/2/19
 */
public class Connection {
    private static final AtomicReference<FutureTask<Connection>> reference = new AtomicReference<>();

    private Connection() {
        System.out.println("构造函数只执行一次");
    }

    public static Connection getInstance() throws Exception {
        FutureTask<Connection> task = reference.get();
        if (task == null) {
            System.out.println(">_<");
            Callable<Connection> callable = Connection::new;
            FutureTask<Connection> newTask = new FutureTask<>(callable);
            // 无论有多少线程，只有一条线程能设置成功
            boolean flag = reference.compareAndSet(null, newTask);
            task = reference.get();
            // 这一句非常重要，如果不执行，则所有的调用者都将处于阻塞状态
            if (flag) newTask.run();
        }
        return task.get();
    }
}
