package com.security.jwt.service.impl.security.handler;

import com.alibaba.fastjson.JSON;
import com.security.jwt.model.RestResult;
import com.security.jwt.model.ReturnCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author miou
 * @date 2019-06-14
 */
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.setHeader("Authorization","");
        PrintWriter out = httpServletResponse.getWriter();
        RestResult result = new RestResult(true, ReturnCode.SystemCode.SYS_SUCCESS.getValue(), "注销成功", null);
        out.write(JSON.toJSONString(result));
        out.flush();
        out.close();
    }
}
