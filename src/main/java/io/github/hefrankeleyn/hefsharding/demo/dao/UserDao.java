package io.github.hefrankeleyn.hefsharding.demo.dao;

import io.github.hefrankeleyn.hefsharding.demo.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {
    
    int insert(User user);
    User findById(Integer id);
    int update(User user);
    int delete(Integer id);
}
