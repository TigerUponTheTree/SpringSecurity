package com.sangeng.Config;

import com.sangeng.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;
    @Bean
    //创建BCryptPasswordEncoder
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //关闭csrf
                .csrf().disable()
                //不通过Session获取SecurityContext，SecurityContext中存储的是认证后的用户信息
                //对于前后端分离的项目不用这个
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 对于登录接口 允许匿名访问，即即使没登录的情况下也能访问/user/login接口
                //放行只会跳过UsernamePasswordAuthenticationFilter
                // 和 BearerTokenAuthenticationFilter这两个过滤器，其它自定义的还是会执行，需要手动在其中放行
                //如果是anonymous表示未登录的用户可以访问该接口，已登录的不能访问，
                // permitall是任何用户都能访问，authenticated是已经登录的用户可以访问
                .antMatchers(HttpMethod.POST, "/user/login").anonymous()
                // 除上面外的所有请求全部需要权限认证，即都要用户认证后才能访问
                //与放行的区别是会走完全部的过滤器，如果未验证会会触发 ExceptionTranslationFilter，
                //返回 401 Unauthorized 或 403 Forbidden 的响应
                .anyRequest().authenticated();
        //添加自定义的过滤器
        //把我们自己定义的对http请求中token进行处理的filter添加到springsecurity框架处理
        //并把我们的过滤器放在UsernamePasswordAuthenticationFilter（框架中第一个过滤器）之前
        http.addFilterBefore(jwtAuthenticationTokenFilter,UsernamePasswordAuthenticationFilter.class);
        //添加注册异常处理器替换默认实现
        //认证失败处理器
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
        //授权失败处理器
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler);

        //允许跨域
        http.cors();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
