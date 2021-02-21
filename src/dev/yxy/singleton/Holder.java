package dev.yxy.singleton;

/**
 * 静态内部类单例模式
 * Created by Nuclear on 2021/2/19
 */
public class Holder {

    public static Holder getInstance() {
        return InnerClass.HOLDER;
    }

    private static class InnerClass {
        private static final Holder HOLDER = new Holder();
    }
}
