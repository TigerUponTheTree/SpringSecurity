package com.sangeng.Controller;

import com.sangeng.Service.login.LoginContext;
import com.sangeng.Service.logout.LogoutService;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {
    @Autowired
    private LogoutService logoutService;
    @Autowired
    private LoginContext loginContext;
    //登录一般都是用post请求
    @PostMapping("/user/login")
    public ResponseResult login(@RequestParam String loginType, @RequestBody User user){
        //登录
        return loginContext.login(loginType, user);
    }
    @RequestMapping("/user/logout")
    public ResponseResult logout(){
        return  logoutService.logout();
    }
}
