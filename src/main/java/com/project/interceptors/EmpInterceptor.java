package com.project.interceptors;

import com.project.untils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.Objects;

@Component
public class EmpInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token=request.getHeader("Authorization");
        try{
            Map<String,Object> claims= JwtUtil.parseToken(token);
            Object employeeRole= claims.get("employeeRole");
            return Objects.equals(employeeRole, "2");
        }
        catch (Exception e){
            response.setStatus(404);
            return false;
        }
    }
}
