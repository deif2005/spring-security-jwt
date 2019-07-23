package com.security.jwt.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Component;

/**
 * @author miou
 * @date 2019-05-13
 */
@Component
public class JwtUtil {
    public String getToken(UserPo user){
        String token;
        token= JWT.create().withAudience(user.getUserName())
                .sign(Algorithm.HMAC256(user.getPassword()));
        return token;
    }

}
