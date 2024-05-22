package com.project.interceptors;

import com.project.untils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;

@Component
public class QueryInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //token保存到前端传送过来的请求头中，名为“Authorization”里面
        String token=request.getHeader("Authorization");
//        try{
            //将token解密出来，同时取出当中包含员工身份的数据，以字符串形式保存下来，同时检查员工身份是否为部门主管
            Map<String,Object> claims= JwtUtil.parseToken(token);
            //String claim= String.valueOf(claims.get("employeeRole"));
            int claim = getAccessLevel(String.valueOf(claims.get("employeeRole")));



            if(request.getRequestURI().matches("/department/.*") && claim > 1)
            {
                //等级达不到1且试图访问department，拦截
                response.setStatus(404);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out=response.getWriter();
                out.print("{\"code\":\"50000\", \"message\":\"权限不足\" }");
                out.flush();
                out.close();
                return false;
            }
            else if ((request.getRequestURI().matches("/employeeBasic/.*") || request.getRequestURI().matches("/employeeJobInfo/.*")) && claim > 2)
            {
                //等级达不到2且试图访问employeeBasic和employeeJobInfo，拦截
                response.setStatus(404);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out=response.getWriter();
                out.print("{\"code\":\"50000\", \"message\":\"权限不足\" }");
                out.flush();
                out.close();
                return false;
            }
            return true;


           /* if(Objects.equals(claim, "1"))
                return true;
            else{
                response.setStatus(404);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out=response.getWriter();
                out.print("{\"code\":\"50000\", \"message\":\"权限不足\" }");
                out.flush();
                out.close();
                return false;
            }*/








            //如果为部门主管，就返回true，允许访问部门相关界面以及使用部门相关功能
//            return Objects.equals(claim, "1");
//        }
//        catch (Exception e){
//            response.setStatus(404);
//            response.setCharacterEncoding("UTF-8");
//            response.setContentType("application/json;charset=utf-8");
//            PrintWriter out=response.getWriter();
//            out.print("{\"code\":\"50000\", \"message\":\"权限不足\" }");
//            out.flush();
//            out.close();
//            return false;
//        }
    }
    private int getAccessLevel(String role) { //一级权限最高
        return switch (role) {
            case "2" -> 2;
            case "1" -> 1;
            default -> 3;
        };
    }
}


