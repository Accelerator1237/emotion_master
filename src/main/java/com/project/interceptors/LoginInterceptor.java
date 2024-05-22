package com.project.interceptors;

import com.project.untils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //token保存到前端传送过来的请求头中，名为“Authorization”里面
        String token=request.getHeader("Authorization");
        try{
            //检查token是否被篡改并且是否为空
            Map<String,Object> claims= JwtUtil.parseToken(token);
            //验证没问题即可正常使用登陆后的功能
            return true;
        }
        catch (Exception e){
            response.setStatus(401);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out=response.getWriter();
            out.print("{\"code\":\"50000\", \"message\":\"未登录\" }");
            out.flush();
            out.close();
            return false;
        }
    }
}
