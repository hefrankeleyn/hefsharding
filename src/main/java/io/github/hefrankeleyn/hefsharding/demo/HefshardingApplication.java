package io.github.hefrankeleyn.hefsharding.demo;

import com.google.common.base.Strings;
import io.github.hefrankeleyn.hefsharding.config.HefShardingAutoConfiguration;
import io.github.hefrankeleyn.hefsharding.config.HefShardingMapperFactoryBean;
import io.github.hefrankeleyn.hefsharding.demo.dao.OrderDao;
import io.github.hefrankeleyn.hefsharding.demo.dao.UserDao;
import io.github.hefrankeleyn.hefsharding.demo.model.Order;
import io.github.hefrankeleyn.hefsharding.demo.model.User;
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

    @Resource
    private OrderDao orderDao;

    @Bean
    public ApplicationRunner runner() {
        return args -> {
            testUser();
            testOrder();
        };
    }

    private void testOrder() {
        for (int i = 0; i < 40; i++) {
            int uid = i<20?0:1;
            Order order = new Order(i, uid, i+0.23);
            // 插入
            int insert = orderDao.insert(order);
            System.out.println(Strings.lenientFormat("===> insert: %s", insert));
            // 查询
            Order currentOrder = orderDao.findById(order.getId(), order.getUid());
            System.out.println("====> findById currentOrder: " + currentOrder);
            // 修改
            order.setPrice(order.getPrice() + 1);
            int update = orderDao.update(order);
            System.out.println("===> update: " + update);
            // 查询
            Order updateOrder = orderDao.findById(order.getId(), order.getUid());
            System.out.println("====> findById updateOrder: " + updateOrder);
            // 删除
            int delete = orderDao.delete(order.getId(), order.getUid());
            System.out.println("===> delete: " + delete);
        }
    }

    private void testUser() {
        for (int i = 0; i < 60; i++) {
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
            int delete = userDao.delete(user.getId());
            System.out.println("===> delete: " + delete);
        }
    }
}
