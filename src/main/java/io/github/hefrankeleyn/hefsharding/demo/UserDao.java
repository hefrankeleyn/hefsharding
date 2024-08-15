package io.github.hefrankeleyn.hefsharding.demo;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {
    
    int insert(User user);
    User findById(Integer id);
    int update(User user);
    int delete(Integer id);
}
