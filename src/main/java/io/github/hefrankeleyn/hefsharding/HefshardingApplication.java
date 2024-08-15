package io.github.hefrankeleyn.hefsharding;

import com.google.common.base.Strings;
import io.github.hefrankeleyn.hefsharding.demo.User;
import io.github.hefrankeleyn.hefsharding.demo.UserDao;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HefshardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(HefshardingApplication.class, args);
    }

    @Resource
    private UserDao userDao;

    @Bean
    public ApplicationRunner runner() {
        return args -> {
            User user = new User(100, "aa", 12);
            // 插入
            int insert = userDao.insert(user);
            System.out.println(Strings.lenientFormat("===> insert: %s", insert));
            // 查询
            User currentUser = userDao.findById(user.getId());
            System.out.println("====> findById currentUser: " + currentUser);
            // 修改
            user.setName("AA02");
            int update = userDao.update(user);
            System.out.println("===> update: " + update);
            // 查询
            User updateUser = userDao.findById(user.getId());
            System.out.println("====> findById updateUser: " + updateUser);
            // 删除
            int delete = userDao.delete(user.getId());
            System.out.println("===> delete: " + delete);
        };
    }
}
