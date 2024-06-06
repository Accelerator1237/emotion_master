package com.project.config;

import com.project.interceptors.EmpInterceptor;
import com.project.interceptors.LoginInterceptor;
import com.project.interceptors.QueryInterceptor;
import com.project.interceptors.SecurityHeadersFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;
    @Autowired
    private QueryInterceptor queryInterceptor;
    @Autowired
    private EmpInterceptor empInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 根据登录返回的token，来拦截未登录的用户
        registry.addInterceptor(loginInterceptor).excludePathPatterns("/employeeBasic/login","/employeeBasic/register","/employeeJobInfo/empJob_add","/employeeBasic/change_password");

        // 根据返回的token中的员工身份，对于非部门主管的人，不允许访问部门管理界面
        registry.addInterceptor(queryInterceptor).addPathPatterns("/department/**","/employeeBasic/**","/employeeJobInfo/**").excludePathPatterns(
                "/employeeBasic/login","/employeeJobInfo/empJob_info","/employeeBasic/info","/employeeBasic/employeeBasic_update","/employee/info_bytoken");

        // registry.addInterceptor(empInterceptor).excludePathPatterns("/department/*");
        // registry.addInterceptor(empInterceptor).excludePathPatterns("/employeeBasic/info_bytoken","/employeeBasic/login","");
    }



}
