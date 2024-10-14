package com.sangeng.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @RequestMapping("/hello")
    //外面已经用双引号了，里面用单引号写访问该接口所需的权限类型
    @PreAuthorize("hasAnyAuthority('system:test:list')")
    public String hello(){
        return "hello";
    }
}
