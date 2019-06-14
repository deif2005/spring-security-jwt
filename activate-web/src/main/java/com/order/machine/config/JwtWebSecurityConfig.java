package com.order.machine.config;

import com.order.machine.service.impl.security.JwtAuthenticationProvider;
import com.order.machine.service.impl.security.handler.*;
import com.order.machine.service.impl.security.component.UserDetailsServiceImpl;
import com.order.machine.service.impl.security.component.AccessDecisionManagerImpl;
import com.order.machine.service.impl.security.component.FilterInvocationSecurityMetadataSourceImpl;
import com.order.machine.service.impl.security.config.JsonLoginConfigurer;
import com.order.machine.service.impl.security.config.JwtConfigurer;
import com.order.machine.service.impl.security.filter.OptionsRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.header.Header;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * @author miou
 * @date 2019-05-15
 * security+jwt总配置文件
 */

@Configuration
@EnableWebSecurity
public class JwtWebSecurityConfig extends WebSecurityConfigurerAdapter {

    final String[] notLoginInterceptPaths = {
            "/user/v1/userLogin",
            "/user/v1/register",
            "/order/v1/activateMachine"
    };

    final String[] adminInterceptPaths = {
            "/admin/**"
    };

    final String[] userInterceptPaths = {
            "/data/**",
            "/order/**"
    };

    //根据一个url请求，获得访问它所需要的roles权限
    @Autowired
    FilterInvocationSecurityMetadataSourceImpl filterInvocationSecurityMetadataSource;
    //接收一个用户的信息和访问一个url所需要的权限，判断该用户是否可以访问
    @Autowired
    AccessDecisionManagerImpl myAccessDecisionManager;
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    CustomAccessDeniedHandler customAccessDeniedHandler;
    @Autowired
    CustomLogoutHandler customLogoutHandler;
    @Autowired
    MyAccessDeniedHandler myAccessDeniedHandler;
    @Autowired
    CustomLogoutSuccessHandler customLogoutSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
            @Override
            public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                o.setSecurityMetadataSource(filterInvocationSecurityMetadataSource);
                o.setAccessDecisionManager(myAccessDecisionManager);
                return o;
            }
        })
                .antMatchers("/image/**").permitAll() //静态资源访问无需认证
                .antMatchers(notLoginInterceptPaths).permitAll()
                .antMatchers(adminInterceptPaths).hasAnyRole("ADMIN") //admin开头的请求，需要admin权限
                .antMatchers(userInterceptPaths).hasRole("USER") //需登陆才能访问的url
                .anyRequest().authenticated()  //默认其它的请求都需要认证，这里一定要添加
        .and()
                .exceptionHandling()
                .accessDeniedHandler(customAccessDeniedHandler) //捕获权限拦截异常
        .and()
                //CSRF禁用，因为不使用session
                .csrf().disable()
                //禁用session
                .sessionManagement().disable()
                //禁用form登录
                .formLogin().disable()
                .cors()  //支持跨域
                //添加header设置，支持跨域和ajax请求
        .and()
                .headers()
                .addHeaderWriter(new StaticHeadersWriter(Arrays.asList(
                        new Header("Access-control-Allow-Origin","*"),
                        new Header("Access-Control-Expose-Headers","Authorization"))))
        .and()  //拦截OPTIONS请求，直接返回header
                .addFilterAfter(new OptionsRequestFilter(),CorsFilter.class)
                //添加登录filter
                .apply(new JsonLoginConfigurer<>())
                .loginSuccessHandler(jsonLoginSuccessHandler())
        .and()  //添加token的filter
                .apply(new JwtConfigurer<>())
                .tokenValidSuccessHandler(jwtRefreshSuccessHandler())
                .permissiveRequestUrls("/logout")
        .and()  //使用默认的logoutFilter
                .logout()
                .logoutUrl("/user/v1/logout")
                //logout时清除token
                .addLogoutHandler(customLogoutHandler)
                .logoutSuccessHandler(customLogoutSuccessHandler)
//                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler()) //logout成功后返回200
        .and()
                .sessionManagement().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider())
                .authenticationProvider(jwtAuthenticationProvider());
    }

    @Bean("daoAuthenticationProvider")
    protected AuthenticationProvider daoAuthenticationProvider() throws Exception{
        //这里会默认使用BCryptPasswordEncoder比对加密后的密码，注意要跟createUser时保持一致
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        daoProvider.setUserDetailsService(userDetailsService());
        return daoProvider;
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    @Bean("jwtAuthenticationProvider")
    protected AuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(userDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    protected LoginAuthenticationSuccessHandler jsonLoginSuccessHandler() {
        return new LoginAuthenticationSuccessHandler(userDetailsService);
    }

    @Bean
    protected JwtRefreshSuccessHandler jwtRefreshSuccessHandler() {
        return new JwtRefreshSuccessHandler(userDetailsService);
    }

    @Bean
    protected TokenClearLogoutHandler tokenClearLogoutHandler() {
        return new TokenClearLogoutHandler(userDetailsService);
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","HEAD", "OPTION"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.addExposedHeader("Authorization");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
