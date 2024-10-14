package com.sangeng.Service.login.Impl;

import com.sangeng.Service.login.LoginStrategy;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.User;
import org.springframework.stereotype.Service;

@Service("sms")
public class SmsLoginStrategy implements LoginStrategy {
    @Override
    public ResponseResult login(User user) {
        //TODO手机验证码登录
        return new ResponseResult(200, "手机验证码登录成功");
    }
}
