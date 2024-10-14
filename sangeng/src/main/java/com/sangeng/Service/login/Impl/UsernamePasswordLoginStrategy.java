package com.sangeng.Service.login.Impl;

import com.sangeng.Service.login.LoginStrategy;
import com.sangeng.Utils.JwtUtil;
import com.sangeng.Utils.RedisCache;
import com.sangeng.domain.LoginUser;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;

@Service("usernamePassword")
public class UsernamePasswordLoginStrategy implements LoginStrategy {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisCache redisCache;
    @Override
    public ResponseResult login(User user) {
        //AuthenticationManager进行用户认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //如果认证没通过，给出对应的提示
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("登录失败");
        }
        //如果认证通过了，使用userid生成一个jwt
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userId);
        //TODO 设置过期时间，以及对token验证的时候刷新token过期的时间
        HashMap<String, String> map = new HashMap<>();
        map.put("token", jwt);
        //把完整的用户信息存入redis   userId作为key
        redisCache.setCacheObject("token:" + userId, loginUser);
        return new ResponseResult(200, "登录成功", map);
    }
}
