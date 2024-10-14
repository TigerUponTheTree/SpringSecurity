package com.sangeng.Service.login.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sangeng.Mapper.MenuMapper;
import com.sangeng.Mapper.UserMapper;
import com.sangeng.domain.LoginUser;
import com.sangeng.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MenuMapper menuMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //查询用户信息
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, username);
        User user = userMapper.selectOne(queryWrapper);
        //如果没有查询到用户就抛出异常
        if (Objects.isNull(user)) {
            //过滤器链中有对异常进行捕获处理（ExceptionTranslationFilter），可以据此对异常进行处理
            throw new RuntimeException("用户名或密码错误");
        }

        //把数据封装成UserDetails对象
        //MyBatis 在查询数据时，如果结果为多条记录,MyBatis 会自动将查询结果封装为一个 List<T>，其中 T 是结果映射的对象类型
        List<String> list = menuMapper.selectPermsByUserId(user.getId());
        return new LoginUser(user, list);
        //return new LoginUser(user);
    }
}
