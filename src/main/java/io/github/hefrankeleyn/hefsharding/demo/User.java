package io.github.hefrankeleyn.hefsharding.demo;

import com.google.common.base.MoreObjects;

/**
 * @Date 2024/8/14
 * @Author lifei
 */
public class User {
    private Integer id;
    private String name;
    private Integer age;

    public User() {
    }

    public User(Integer id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(User.class)
                .add("id", id)
                .add("name", name)
                .add("age", age)
                .toString();
    }
}
