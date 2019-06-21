package com.order.machine.service.impl.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.order.machine.service.impl.security.component.JwtAuthenticationToken;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author miou
 * @date 2019-05-14
 * jwt认证过滤器:用于判断请求是否需要验证以及请求中的token信息是否合法
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private RequestMatcher requiresAuthenticationRequestMatcher;
    private AuthenticationManager authenticationManager;
    private List<RequestMatcher> permissiveRequestMatchers;
    private AuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
    private AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

    /**
     * 拦截带authorization的请求
     */
    public JwtAuthenticationFilter() {
        this.requiresAuthenticationRequestMatcher = new RequestHeaderRequestMatcher("Authorization");
    }

    /**
     * 获取header中authorization的token值
     * @param request
     * @return
     */
    protected String getJwtToken(HttpServletRequest request) {
        String authInfo = request.getHeader("Authorization");
        return StringUtils.removeStart(authInfo, "Bearer ");
    }

    /**
     * 匹配请求参数
     * @param request
     * @param response
     * @return
     */
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return requiresAuthenticationRequestMatcher.matches(request);
    }

    /**
     * 认证请求信息是否合法
     * 利用AuthenticationManager中的AuthenticationProvider验证token
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //header没带token的，直接放过，因为部分url匿名用户也可以访问
        //如果不支持不带token的匿名用户的请求，这里放过也没问题，
        //因为SecurityContext中没有认证信息，后面会被权限控制模块拦截
        Authentication authResult = null;
        AuthenticationException failed = null;
        if (!requiresAuthentication(request, response)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            //从头中获取token并封装后提交给AuthenticationManager
            String token = getJwtToken(request);
            if(StringUtils.isNotBlank(token)) {
                JwtAuthenticationToken authToken = new JwtAuthenticationToken(JWT.decode(token));
                authResult = this.getAuthenticationManager().authenticate(authToken);
            } else {  //如果token长度为0
                failed = new InsufficientAuthenticationException("JWT is Empty");
            }
        } catch(JWTDecodeException e) {
            logger.error("JWT format error", e);
            failed = new InsufficientAuthenticationException("JWT format error", e);
        }catch (InternalAuthenticationServiceException e) {
            logger.error("An internal error occurred while trying to authenticate the user.", failed);
            failed = e;
        }catch (AuthenticationException e) {
            failed = e;
        }
        if(authResult != null) {   //token认证成功
            successfulAuthentication(request, response, filterChain, authResult);
        } else if(!permissiveRequest(request)){
            //token认证失败，并且这个request不在例外列表里，才会返回错误
            unsuccessfulAuthentication(request, response, failed);
            return;
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 认证成功
     * 设置securitycontext
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     * @throws ServletException
     */
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException, ServletException{
        SecurityContextHolder.getContext().setAuthentication(authResult);
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    /**
     * 设置不拦截请求
     * @param request
     * @return
     */
    protected boolean permissiveRequest(HttpServletRequest request) {
        if(permissiveRequestMatchers == null)
            return false;
        for(RequestMatcher permissiveMatcher : permissiveRequestMatchers) {
            if(permissiveMatcher.matches(request))
                return true;
        }
        return false;
    }

    /**
     * 设置认证失败处理
     * @param request
     * @param response
     * @param failed
     * @throws IOException
     * @throws ServletException
     */
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setSuccessHandler(AuthenticationSuccessHandler successHandler) {
        Assert.notNull(successHandler, "successHandler cannot be null");
        this.successHandler = successHandler;
    }

    public void setFailureHandler(AuthenticationFailureHandler failureHandler) {
        Assert.notNull(failureHandler, "failureHandler cannot be null");
        this.failureHandler = failureHandler;
    }

    public void setPermissiveRequestMatchers(String... urls) {
        if(permissiveRequestMatchers == null)
            permissiveRequestMatchers = new ArrayList<>();
        for(String url : urls)
            permissiveRequestMatchers .add(new AntPathRequestMatcher(url));
    }
}
