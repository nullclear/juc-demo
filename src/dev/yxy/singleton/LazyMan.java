package dev.yxy.singleton;

/**
 * DCL单例模式
 * Created by Nuclear on 2021/2/19
 */
public class LazyMan {
    private static boolean flag = true;

    public LazyMan() {
        synchronized (LazyMan.class) {
            if (flag) flag = false;
            else throw new RuntimeException("[非法反射]破坏单例模式");
        }
    }

    private volatile static LazyMan lazyMan;

    public static LazyMan getInstance() {
        if (lazyMan == null) {
            synchronized (LazyMan.class) {
                if (lazyMan == null) lazyMan = new LazyMan();
            }
        }
        return lazyMan;
    }
}
