package com.sangeng.Service.login;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.User;

public interface LoginStrategy {
    ResponseResult login(User user);
}
