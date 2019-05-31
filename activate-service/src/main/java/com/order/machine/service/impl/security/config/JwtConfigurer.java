package com.order.machine.service.impl.security.config;

import com.order.machine.service.impl.security.LoginAuthenticationFailureHandler;
import com.order.machine.service.impl.security.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;

/**
 * @author miou
 * @date 2019-05-15
 * 配置jwt校验的filter
 */
public class JwtConfigurer<T extends JwtConfigurer<T,B>, B extends HttpSecurityBuilder<B>> extends
        AbstractHttpConfigurer<T, B> {

    private JwtAuthenticationFilter authFilter;

    public JwtConfigurer(){
        this.authFilter = new JwtAuthenticationFilter();
    }

    @Override
    public void configure(B http) throws Exception {
        authFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        authFilter.setFailureHandler(new LoginAuthenticationFailureHandler());
        //将filter放到logoutFilter之前
        JwtAuthenticationFilter filter = postProcess(authFilter);
        http.addFilterBefore(filter, LogoutFilter.class);
    }

    /**
     * 设置匿名用户可访问url
     * @param urls
     * @return
     */
    public JwtConfigurer<T, B> permissiveRequestUrls(String ... urls){
        authFilter.setPermissiveRequestMatchers(urls);
        return this;
    }

    public JwtConfigurer<T, B> tokenValidSuccessHandler(AuthenticationSuccessHandler successHandler){
        authFilter.setSuccessHandler(successHandler);
        return this;
    }
}
