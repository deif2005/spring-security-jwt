package com.order.machine.service.impl.security.handler;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.order.machine.common_const.CommonConst;
import com.order.machine.service.impl.security.component.UserDetailsServiceImpl;
import com.order.machine.service.impl.security.config.JwtAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author miou
 * @date 2019-05-15
 * jwt刷新,jwt的刷新和salt盐值的留存时间不同，jwt刷新是限制jwt被盗用后的时长
 * salt盐值是用户登录后的保活时长，失效后需要重新登录
 */
public class JwtRefreshSuccessHandler implements AuthenticationSuccessHandler {

    private UserDetailsServiceImpl userDetailsService;

    public JwtRefreshSuccessHandler(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * token验证成功后的回调，用于处理验证码刷新等业务
     * @param httpServletRequest
     * @param response
     * @param authentication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        DecodedJWT jwt = ((JwtAuthenticationToken)authentication).getToken();
        boolean shouldRefresh = shouldTokenRefresh(jwt.getIssuedAt());
        if(shouldRefresh) {
            String newToken = userDetailsService.saveUserLoginInfo((UserDetails)authentication.getPrincipal());
            response.setHeader("Authorization", newToken);
        }
    }

    /**
     * 是否刷新验证信息
     * @param issueAt
     * @return
     */
    protected boolean shouldTokenRefresh(Date issueAt){
        LocalDateTime issueTime = LocalDateTime.ofInstant(issueAt.toInstant(), ZoneId.systemDefault());
        return LocalDateTime.now().minusSeconds(CommonConst.TOKEN_REFRESH_INTERVAL).isAfter(issueTime);
    }
}
