package com.order.machine.service.impl.security.handler;

import com.alibaba.fastjson.JSON;
import com.order.machine.common_const.CommonEnum;
import com.order.machine.model.RestResult;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author miou
 * @date 2019-06-14
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,AccessDeniedException
            accessDeniedException) throws IOException, ServletException {
        //系统级异常，错误码固定为-1，提示语固定为系统繁忙，请稍后再试
        RestResult result = new RestResult(false, CommonEnum.ReturnCode.SystemCode.sys_err_noauth.getValue(),
                null, CommonEnum.ReturnMsg.SystemMsg.sys_err_noauth.getValue());
        response.setContentType("application/json");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(result));
    }

}
