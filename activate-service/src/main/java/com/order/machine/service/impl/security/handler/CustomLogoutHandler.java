package com.order.machine.service.impl.security.handler;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.order.machine.redis.RedisConstants;
import com.order.machine.redis.RedisUtil;
import com.order.machine.service.impl.security.config.JwtAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author miou
 * @date 2019-06-14
 */
@Component
public class CustomLogoutHandler implements LogoutHandler {

    @Autowired
    RedisUtil redisUtil;

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                       Authentication authentication) {
        DecodedJWT jwt = ((JwtAuthenticationToken)authentication).getToken();
        String userName = jwt.getSubject();
        redisUtil.del(String.format(RedisConstants.LOGIN_TOKEN,userName));
    }
}
