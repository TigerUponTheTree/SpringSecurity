package com.sangeng.Service.logout;

import com.sangeng.Utils.RedisCache;
import com.sangeng.domain.LoginUser;
import com.sangeng.domain.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class LogoutService {
    @Autowired
    private RedisCache redisCache;
    public ResponseResult logout() {
        //获取SecurityContextHolder中的用户id

        //注意：SecurityContextHolder中的值不需要删除，因为对于每一个到来的请求都会经过处理器链生成
        //对应的SecurityContextHolder，当前处理结束后别的请求再来会有它们自己的SecurityContextHolder
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser)authentication.getPrincipal();
        //这里的loginUser是不可能为空的，如果authentication为空即传来来的token为空，在前面的拦截器部分就以及被拦截住了
        Long userId = loginUser.getUser().getId();

        //删除redis中的值
        redisCache.deleteObject("token:"+userId);
        return new ResponseResult(200,"注销成功");
    }
}
