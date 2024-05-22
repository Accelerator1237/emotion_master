package com.project.untils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class JwtUtil {
    private static final String KEY="mood";

    //创建token
    public static String genToken(Map<String, Object>claims){
        return JWT.create()
                .withClaim("employeeId",claims) //保存员工id
                .withClaim("employeeRole",claims) //保存员工身份
                .withExpiresAt(new Date(System.currentTimeMillis()+1000*60*60)) //token有效时间为一小时
                .sign(Algorithm.HMAC256(KEY));  //加密算法，同时设置密钥为mood
    }


    //验证token
    public static Map<String,Object> parseToken(String token){
        return JWT.require(Algorithm.HMAC256(KEY))   //利用密钥验证token
                .build()
                .verify(token)
                .getClaim("employeeRole")  //取出包含员工身份的数据方便权限验证
                .asMap();
    }

    public static Map<String,Object> parseTokenToEmployeeId(String token){
        return JWT.require(Algorithm.HMAC256(KEY))   //利用密钥验证token
                .build()
                .verify(token)
                .getClaim("employeeId")  //取出包含员工身份的数据方便权限验证
                .asMap();
    }
}
