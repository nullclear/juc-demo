package dev.yxy.atom;

/**
 * Created by yuanxy on 2021/10/09
 */
public class User {

    public volatile String name;

    public volatile Integer age;

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
}
