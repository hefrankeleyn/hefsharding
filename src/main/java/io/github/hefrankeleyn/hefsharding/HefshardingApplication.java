package io.github.hefrankeleyn.hefsharding;

import com.google.common.base.Strings;
import io.github.hefrankeleyn.hefsharding.core.HefShardingAutoConfiguration;
import io.github.hefrankeleyn.hefsharding.core.HefShardingMapperFactoryBean;
import io.github.hefrankeleyn.hefsharding.demo.User;
import io.github.hefrankeleyn.hefsharding.demo.UserDao;
import jakarta.annotation.Resource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({HefShardingAutoConfiguration.class})
@MapperScan(value = "io.github.hefrankeleyn.hefsharding.demo", factoryBean = HefShardingMapperFactoryBean.class)
public class HefshardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(HefshardingApplication.class, args);
    }

    @Resource
    private UserDao userDao;

    @Bean
    public ApplicationRunner runner() {
        return args -> {
            for (int i = 0; i < 10; i++) {
                User user = new User(i, "aa"+i, 12);
                // 插入
                int insert = userDao.insert(user);
                System.out.println(Strings.lenientFormat("===> insert: %s", insert));
                // 查询
                User currentUser = userDao.findById(user.getId());
                System.out.println("====> findById currentUser: " + currentUser);
                // 修改
                user.setName(user.getName() + "-02");
                int update = userDao.update(user);
                System.out.println("===> update: " + update);
                // 查询
                User updateUser = userDao.findById(user.getId());
                System.out.println("====> findById updateUser: " + updateUser);
                // 删除
//                int delete = userDao.delete(user.getId());
//                System.out.println("===> delete: " + delete);
            }


        };
    }
}
