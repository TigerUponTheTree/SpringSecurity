package com.sangeng;

import com.sangeng.Mapper.MenuMapper;
import com.sangeng.Mapper.UserMapper;
import com.sangeng.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

//@SpringBootTest
public class MapperTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MenuMapper menuMapper;

    @Test
    public void testUserMapper(){
        List<User> users = userMapper.selectList(null);
        System.out.println(users);

    }
    @Test
    public void PasswordEncoder(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("123"));
        passwordEncoder.matches("123","$2a$10$JOGdcqutJ/NVkya8yUhXK.ySsCMMVKLMA0iZwQNQb5NkaXicQpOKO");

    }

    @Test
    public void testSelectPermsByUserid(){
        System.out.println(menuMapper.selectPermsByUserId(2L));

    }


}
