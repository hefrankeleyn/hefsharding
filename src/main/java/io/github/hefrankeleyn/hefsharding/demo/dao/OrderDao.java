package io.github.hefrankeleyn.hefsharding.demo.dao;

import io.github.hefrankeleyn.hefsharding.demo.model.Order;
import io.github.hefrankeleyn.hefsharding.demo.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDao {
    
    int insert(Order order);
    Order findById(Integer id, Integer uid);
    int update(Order order);
    int delete(Integer id, Integer uid);
}
