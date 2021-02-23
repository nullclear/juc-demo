package dev.yxy.daemon;

/**
 * 线程隔离的守护线程持有者
 * Created by Nuclear on 2021/2/23
 */
public class DaemonContextHolder {
    private static final ThreadLocal<Daemon> contextHolder = new InheritableThreadLocal<>();

    /**
     * 启动守护线程
     */
    public static void start() {
        Daemon daemon = getContext();
        if (daemon != null) {
            daemon.start();
        } else {
            throw new IllegalStateException("no daemon in the context");
        }
    }

    /**
     * 启动守护线程
     *
     * @param daemon 守护线程实例
     */
    public static void start(Daemon daemon) {
        setContext(daemon);
        daemon.start();
    }

    /**
     * 取消守护线程的任务
     */
    public static void cancel() {
        Daemon daemon = getContext();
        if (daemon != null) {
            daemon.cancel();
        }
        clearContext();
    }

    /**
     * 设置守护线程实例
     *
     * @param daemon 守护线程实例
     */
    public static void setContext(Daemon daemon) {
        if (daemon != null) {
            contextHolder.set(daemon);
        } else {
            throw new IllegalArgumentException("daemon can't be null");
        }
    }

    private static Daemon getContext() {
        return contextHolder.get();
    }

    private static void clearContext() {
        contextHolder.remove();
    }
}
