package com.project;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTtest {
    @Test
    public void testGen(){
        Map<String,Object> claims=new HashMap<>();
        claims.put("employeeRole",1);
        claims.put("employeeId",1);
        String token=JWT.create()
                .withClaim("employeeRole",1)
                .withClaim("employeeId",1)
                .withExpiresAt(new Date(System.currentTimeMillis()+1000*60*60))
                .sign(Algorithm.HMAC256("mood"));
        System.out.println(token);
    }

    @Test
    public void testParse(){
        String token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbXBsb3llZVJvbGUiOjEsImVtcGxveWVlSWQiOjEsImV4cCI6MTcxMjYzMzEyNX0.76Wt3HS5Ir3yzr1wOeLtFx26tORqamIdi75l2eQE1Co";
        String s= String.valueOf(JWT.require(Algorithm.HMAC256("mood"))
                .build()
                .verify(token)
                .getClaim("employeeRole"));

        System.out.println(s);
    }
}
