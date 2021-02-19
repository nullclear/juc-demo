package dev.yxy.singleton;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 非法反射可以破坏所谓的单例模式
 * Created by Nuclear on 2021/2/19
 */
public class TestLazyMan {
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        LazyMan man = LazyMan.getInstance();
        System.out.println("man = " + man);

        Constructor<LazyMan> constructor = LazyMan.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        LazyMan lazyMan = constructor.newInstance();
        System.out.println("lazyMan = " + lazyMan);
    }
}
