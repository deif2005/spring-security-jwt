package com.security.jwt.service.impl.security.handler;

import com.alibaba.fastjson.JSON;
import com.security.jwt.common_const.CommonEnum;
import com.security.jwt.model.RestResult;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author miou
 * @date 2019-05-13
 */
@Component
public class LoginAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        PrintWriter out = httpServletResponse.getWriter();
        StringBuffer sb = new StringBuffer();
        //系统级异常，错误码固定为-1，提示语固定为系统繁忙，请稍后再试
        RestResult result = new RestResult(false, CommonEnum.ReturnCode.SystemCode.sys_err_exception.getValue(),
                null, CommonEnum.ReturnMsg.SystemMsg.sys_err_exception.getValue());
        if (e instanceof UsernameNotFoundException) {
            result.setCode(CommonEnum.ReturnCode.UserLoginCode.user_login_userorpassword_error.getValue());
            result.setErrorMessage("用户名或密码输入错误，登录失败!");
        } else if (e instanceof BadCredentialsException || e instanceof NonceExpiredException){
            result.setCode(CommonEnum.ReturnCode.UserLoginCode.user_login_overdue_error.getValue());
            result.setErrorMessage("用户验证信息已失效!");
        } else if (e instanceof DisabledException) {
            result.setCode(CommonEnum.ReturnCode.UserLoginCode.user_account_expired.getValue());
            result.setErrorMessage("账户被禁用，登录失败，请联系管理员!");
        }
        else {
            result.setCode(CommonEnum.ReturnCode.SystemCode.sys_err_tokeninvalid.getValue());
            result.setErrorMessage("无效JWT");
        }
        out.write(JSON.toJSONString(result));
        out.flush();
        out.close();
    }
}
