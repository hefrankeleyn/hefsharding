package io.github.hefrankeleyn.hefsharding.demo.model;

import com.google.common.base.MoreObjects;

/**
 * @Date 2024/8/23
 * @Author lifei
 */
public class Order {
    private Integer id;
    private Integer uid;
    private Double price;

    public Order() {
    }

    public Order(Integer id, Integer uid, Double price) {
        this.id = id;
        this.uid = uid;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(Order.class)
                .add("id", id)
                .add("uid", uid)
                .add("price", price)
                .toString();
    }
}
