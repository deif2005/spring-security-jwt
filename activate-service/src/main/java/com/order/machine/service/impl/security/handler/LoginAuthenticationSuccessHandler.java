package com.order.machine.service.impl.security.handler;

import com.alibaba.fastjson.JSON;
import com.order.machine.model.RestResult;
import com.order.machine.model.ReturnCode;
import com.order.machine.po.MenuPo;
import com.order.machine.service.impl.security.component.UserDetailsImpl;
import com.order.machine.service.impl.security.component.UserDetailsServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author miou
 * @date 2019-05-13
 * 登录成功处理类
 */
public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private UserDetailsServiceImpl userDetailsService;

    public LoginAuthenticationSuccessHandler(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * 生成jwt，并返回
     * @param httpServletRequest
     * @param httpServletResponse
     * @param authentication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException, ServletException {
        UserDetailsImpl userDetails = (UserDetailsImpl)authentication.getPrincipal();
        String jwt = userDetailsService.saveUserLoginInfo(userDetails);
        List<MenuPo> menuPoList = userDetailsService.getUserMenu(userDetails.getUsername());
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.setHeader("Authorization",jwt);
        PrintWriter out = httpServletResponse.getWriter();
        RestResult result = new RestResult(true, ReturnCode.SystemCode.SYS_SUCCESS.getValue(), menuPoList, null);
        out.write(JSON.toJSONString(result));
        out.flush();
        out.close();
    }
}
