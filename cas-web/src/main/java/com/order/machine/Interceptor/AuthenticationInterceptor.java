package com.order.machine.Interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.order.machine.common_const.CommonEnum;
import com.order.machine.exception.LogicException;
import com.order.machine.jwt.PassToken;
import com.order.machine.jwt.UserLoginToken;
import com.order.machine.mapper.UserMapper;
import com.order.machine.po.UserPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author miou
 * @date 2019-05-13
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor { //OncePerRequestFilter

    @Autowired
    UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");// 从 http 请求头中取出 token
        // 如果不是映射到方法直接通过
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod handlerMethod=(HandlerMethod)handler;
        Method method=handlerMethod.getMethod();
        //检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                return true;
            }
        }
        //检查有没有需要用户权限的注解
        if (method.isAnnotationPresent(UserLoginToken.class)) {
            UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
            if (userLoginToken.required()) {
                // 执行认证
                if (null == token)
                    throw LogicException.le(CommonEnum.ReturnCode.SystemCode.sys_err_tokeninvalid.getValue(),
                            "非法请求");
            }
            //获取token中的user id
            String userId;
            try {
                userId = JWT.decode(token).getAudience().get(0);
            } catch (JWTDecodeException j) {
                throw LogicException.le(CommonEnum.ReturnCode.SystemCode.sys_err_noauth.getValue(),
                        CommonEnum.ReturnMsg.SystemMsg.sys_err_noauth.getValue());
            }
            UserPo user = userMapper.selectOne (new UserPo(userId,null));
            if (user == null) {
                throw LogicException.le(CommonEnum.ReturnCode.UserLoginCode.user_login_userorpassword_error.getValue(),
                        "用户未注册");
            }
            // 验证 token
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
            try {
                jwtVerifier.verify(token);
            } catch (JWTVerificationException e) {
                throw LogicException.le(CommonEnum.ReturnCode.SystemCode.sys_err_tokeninvalid.getValue(),
                        CommonEnum.ReturnMsg.SystemMsg.sys_err_tokeninvalid.getValue());
            }
            return true;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
