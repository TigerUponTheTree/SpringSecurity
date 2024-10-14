package com.sangeng.handler;

import com.alibaba.fastjson.JSON;
import com.sangeng.domain.ResponseResult;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        //处理异常
        ResponseResult<Object> result = new ResponseResult<>(HttpStatus.FORBIDDEN.value(), "您的权限不足");
        String json = JSON.toJSONString(result);
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        //用 response.getWriter().print()也可以，其会自动对要写入的内容进行toString操作，但是多了一步格式检查性能有一点降低
        httpServletResponse.getWriter().write("{\"message\":\"" + json + "\"}");
    }
}
