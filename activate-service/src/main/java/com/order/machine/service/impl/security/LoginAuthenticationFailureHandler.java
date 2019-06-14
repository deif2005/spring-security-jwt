package com.order.machine.service.impl.security;

import com.alibaba.fastjson.JSON;
import com.order.machine.common_const.CommonEnum;
import com.order.machine.exception.LogicException;
import com.order.machine.model.RestResult;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
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
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        PrintWriter out = httpServletResponse.getWriter();
        StringBuffer sb = new StringBuffer();
        //系统级异常，错误码固定为-1，提示语固定为系统繁忙，请稍后再试
        RestResult result = new RestResult(false, CommonEnum.ReturnCode.SystemCode.sys_err_exception.getValue(),
                null, CommonEnum.ReturnMsg.SystemMsg.sys_err_exception.getValue());
//        sb.append("{\"status\":\"error\",\"msg\":\"");
        if (e instanceof UsernameNotFoundException || e instanceof BadCredentialsException) {
            result.setCode(CommonEnum.ReturnCode.UserLoginCode.user_login_userorpassword_error.getValue());
            result.setErrorMessage("用户名或密码输入错误，登录失败!");
//            sb.append("用户名或密码输入错误，登录失败!");
        } else if (e instanceof DisabledException) {
            result.setCode(CommonEnum.ReturnCode.UserLoginCode.user_account_expired.getValue());
            result.setErrorMessage("账户被禁用，登录失败，请联系管理员!");
//            sb.append("账户被禁用，登录失败，请联系管理员!");
        } else {
            result.setCode(CommonEnum.ReturnCode.SystemCode.sys_err_tokeninvalid.getValue());
            result.setErrorMessage("非法访问");
//            LogicException.le(CommonEnum.ReturnCode.SystemCode.sys_err_tokeninvalid.getValue(),"非法访问");
//            sb.append("非法访问");
        }
//        sb.append("\"}");
//        out.write(sb.toString());
        out.write(JSON.toJSONString(result));
        out.flush();
        out.close();
    }
}
