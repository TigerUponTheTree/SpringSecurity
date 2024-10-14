package com.sangeng.domain;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class LoginUser implements UserDetails {
    //用lombook方法生成对应的getter/setter
    private User user;

    //用来存放权限信息，还要改写getAuthorities方法，因为这个是框架调用获取权限的
    private List<String> permission;

    public LoginUser(User user, List<String> permission) {
        this.user = user;
        this.permission = permission;
    }

    //redis出于安全考虑，是不会将GrantedAuthority进行序列化的，会报异常
    //只需要把permission存入即可，到时候从redis中取出还是会构建所需要的集合
    //因此给其添加fastJson的注解，让authorities不进行序列化到redis中
    @JSONField(serialize = false)
    private List<GrantedAuthority> authorities;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(authorities != null){
            return authorities;
        }
        //List<GrantedAuthority> newList = new ArrayList<>();
        //把permission中的权限信息封装成SimpleGrantedAuthority对象
        //因为要求返回一个集合，且其中的类型为GrantedAuthority的拓展类
        authorities = new ArrayList<>();
        for(String permission:permission){
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(permission);
            //newList.add(authority);  因为不想每次获取时都进行转换，因此考虑将其设置为成员变量
            authorities.add(authority);
        }
        /*List<SimpleGrantedAuthority> newList = permission.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());*/
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
