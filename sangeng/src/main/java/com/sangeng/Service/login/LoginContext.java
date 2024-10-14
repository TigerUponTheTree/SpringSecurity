package com.sangeng.Service.login;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LoginContext {
    private final Map<String, LoginStrategy> loginStrategyMap;

    @Autowired
    public LoginContext(Map<String, LoginStrategy> loginStrategyMap) {
        this.loginStrategyMap = loginStrategyMap;
    }

    /**
     * 根据登录类型选择对应的策略
     */
    public ResponseResult login(String loginType, User user) {
        LoginStrategy loginStrategy = loginStrategyMap.get(loginType);

        if (loginStrategy == null) {
            throw new IllegalArgumentException("不支持的登录类型: " + loginType);
        }

        return loginStrategy.login(user);
    }
}
