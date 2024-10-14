package com.sangeng.filter;

import com.sangeng.Utils.JwtUtil;
import com.sangeng.Utils.RedisCache;
import com.sangeng.domain.LoginUser;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

//对于实现 Filter 并使用 @Component 的过滤器会被自动调用
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        //TODO 可以将放行路径写在配置文件当中，或者分离公共和受保护资源的过滤器
        //因为这是我们自定义的过滤器，即便在配置中放行了login接口，该过滤器还是会被调用
        //放行只是不调用框架自带的一部分过滤器
        if (httpServletRequest.getRequestURI().equals("/user/login")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse); // 放行
            return;
        }
        //获取token
        String token = httpServletRequest.getHeader("token");
        if (!StringUtils.hasText(token)) {
            //放行
            //因为对于不含token的请求，过滤器会自动往下调用，直到如FilterSecurityInterceptor这种过滤器，其会抛出异常等
            //filterChain.doFilter(httpServletRequest, httpServletResponse);
            //过滤器在沿着过滤器链执行完毕之后还会返回继续执行，因为这里后面都是token解析的代码
            //对于返回来之后我们并不需要再对其执行，直接用return返回防止过滤器执行结束后继续执行后面的代码

            // 用户未登录，返回401状态码和错误信息
            //Spring Security 默认只处理身份验证和授权，如果请求中没有 token，并不会主动返回错误响应。
            //这意味着请求会继续流动，直到后续的过滤器或控制器可能返回相应的错误，可能造成不必要资源浪费。
            // token为空，处理逻辑
            handleUnauthorized(httpServletResponse, "用户未登录");
            return;
        }

        //解析token
        String userid;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userid = claims.getSubject();
            // 检查token是否过期
            //TODO 对于快过期的token调用刷新接口获取新的Token，提示写成一个枚举类不要硬编码
            if (claims.getExpiration().before(new Date())) {
                handleUnauthorized(httpServletResponse, "token已过期");
                return;
            }
        } catch (Exception e) {
            //TODO 重定向到登录界面？
            handleUnauthorized(httpServletResponse, "token非法");
            return;

            //Spring 的异常处理机制会捕获到这个异常，并根据其处理逻辑进行处理。
            //这可能会导致在异常处理代码中设置的 HTTP 响应头和消息被忽略，spring的异常处理
            //机制不会返回http响应
            //throw new RuntimeException("token非法");

        }

        //从redis中获取用户信息
        String redisKey = "token:" + userid;
        LoginUser loginUser = (LoginUser) redisCache.getCacheObject(redisKey);

        if (Objects.isNull(loginUser)) {
            //TODO redis未查询到，可能是redis数据过期，重定性到登录接口，如果由数据库结合重新查询数据库
            handleUnauthorized(httpServletResponse, "用户未登录或会话过期");
        }
        //获取权限信息封装到Authentication
        //存入SecurityContextHolder
        //先建立一个authentication变量封装loginUser（setAuthentication接收的是authentication）
        //这里选用三个参数的构造函数，因为三参数的构造函数有super.setAuthenticated(true)
        //这句把Authenticated设置为true，表示该用户已经认证通过了，后续的过滤器发现是已认证的状态就不会再做其它处理了
        //UsernamePasswordAuthenticationToken也在登录的部分用到，那里用的是双参数的构造
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        //放行
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    // 处理未授权的请求
    private void handleUnauthorized(HttpServletResponse response, String message) throws IOException {
        //TODO 这段代码在handler中也有，考虑做成工具类WebUtiles renderString方法
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        //用 response.getWriter().print()也可以，其会自动对要写入的内容进行toString操作，但是多了一步格式检查性能有一点降低
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
