package dev.yxy.delay;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * 延时任务管理者
 *
 * @author nullclear
 * @since 2023-02-02
 */
public class DelayTaskManager {

    private final ReentrantLock mainLock = new ReentrantLock();
    private final HashSet<Worker> workers = new HashSet<>();
    private final AtomicInteger workerCounter = new AtomicInteger(0);
    private long completedTaskCount;
    private final int maximumPoolSize;
    private final Function<Runnable, Thread> threadSupplier;
    private final BlockingQueue<DelayTask> delayQueue;

    public DelayTaskManager(int maximumPoolSize, Function<Runnable, Thread> threadSupplier) {
        this(maximumPoolSize, threadSupplier, new DelayQueue<>());
    }

    public DelayTaskManager(int maximumPoolSize, Function<Runnable, Thread> threadSupplier, BlockingQueue<DelayTask> delayQueue) {
        if (maximumPoolSize <= 0) {
            throw new IllegalArgumentException();
        }
        this.maximumPoolSize = maximumPoolSize;
        this.threadSupplier = Objects.requireNonNull(threadSupplier);
        this.delayQueue = Objects.requireNonNull(delayQueue);
    }

    public boolean submit(Runnable task, Duration delayTime) {
        Objects.requireNonNull(task);
        Objects.requireNonNull(delayTime);
        if (this.workerCounter.get() < this.maximumPoolSize) {
            this.addWorker();
        }
        return this.delayQueue.offer(new DelayTask(task, delayTime));
    }

    public void shutdown() {
        ReentrantLock mainLock = this.mainLock;
        try {
            mainLock.lock();
            this.workers.forEach(Worker::shutdown);
        } finally {
            mainLock.unlock();
        }
    }

    public List<DelayTask> shutdownNow() {
        List<DelayTask> tasks;
        ReentrantLock mainLock = this.mainLock;
        try {
            mainLock.lock();
            this.workers.forEach(Worker::shutdown);
            tasks = this.drainQueue();
        } finally {
            mainLock.unlock();
        }
        return tasks;
    }

    public int getActiveCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            return workers.size();
        } finally {
            mainLock.unlock();
        }
    }

    public long getTaskCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = this.completedTaskCount;
            for (Worker w : this.workers) {
                n += w.completedTasks;
            }
            return n + this.delayQueue.size();
        } finally {
            mainLock.unlock();
        }
    }

    public long getCompletedTaskCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = this.completedTaskCount;
            for (Worker w : this.workers) {
                n += w.completedTasks;
            }
            return n;
        } finally {
            mainLock.unlock();
        }
    }

    private void addWorker() {
        ReentrantLock mainLock = this.mainLock;
        try {
            mainLock.lock();
            if (this.workerCounter.get() < this.maximumPoolSize) {
                Worker worker = new Worker();
                this.workers.add(worker);
                worker.start();
                this.workerCounter.incrementAndGet();
            }
        } finally {
            mainLock.unlock();
        }
    }

    private void runWorker(Worker w) {
        boolean completedAbruptly = true;
        try {
            while (!w.shutdown) {
                DelayTask task = this.delayQueue.take();
                task.run();
                //noinspection NonAtomicOperationOnVolatileField
                w.completedTasks++;
            }
            completedAbruptly = false;
        } catch (InterruptedException ignored) {

        } finally {
            this.processWorkerExit(w, completedAbruptly);
        }
    }

    private void processWorkerExit(Worker w, boolean completedAbruptly) {
        if (completedAbruptly) {
            this.workerCounter.decrementAndGet();
        }
        ReentrantLock mainLock = this.mainLock;
        try {
            mainLock.lock();
            w.terminated = true;
            this.completedTaskCount += w.completedTasks;
            this.workers.remove(w);
        } finally {
            mainLock.unlock();
        }
    }

    private List<DelayTask> drainQueue() {
        BlockingQueue<DelayTask> q = this.delayQueue;
        ArrayList<DelayTask> taskList = new ArrayList<>();
        q.drainTo(taskList);
        if (!q.isEmpty()) {
            for (DelayTask r : q.toArray(new DelayTask[0])) {
                if (q.remove(r)) {
                    taskList.add(r);
                }
            }
        }
        return taskList;
    }

    private class Worker implements Runnable {

        private final Thread thread;
        private volatile boolean started;
        private volatile boolean shutdown;
        private volatile boolean terminated;
        private volatile long completedTasks;

        public Worker() {
            this.thread = DelayTaskManager.this.threadSupplier.apply(this);
        }

        @Override
        public void run() {
            DelayTaskManager.this.runWorker(this);
        }

        public void start() {
            if (this.started || this.terminated) {
                throw new IllegalStateException("Worker has been started or closed.");
            }
            this.thread.start();
            this.started = true;
        }

        public void shutdown() {
            if (!this.started) {
                throw new IllegalStateException("Worker has been not started.");
            }
            this.shutdown = true;
        }
    }
}
