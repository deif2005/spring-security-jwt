package com.order.machine.service.impl.security;

import com.alibaba.fastjson.JSON;
import com.order.machine.model.RestResult;
import com.order.machine.model.ReturnCode;
import com.order.machine.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
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
public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

//    @Autowired
//    RedisUtil redisUtil;
//    @Autowired
//    JwtUtil jwtUtil;
//    @Autowired
//    UserDetailsServiceImpl userDetailsService;

    private UserDetailsServiceImpl userDetailsService;

    public LoginAuthenticationSuccessHandler(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException, ServletException {
        UserDetailsImpl userDetails = (UserDetailsImpl)authentication.getPrincipal();
//        String jwt = jwtUtil.getToken(new UserPo(null,userDetails.getUsername(),userDetails.getPassword()));
        String jwt = userDetailsService.saveUserLoginInfo(userDetails);
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.setHeader("Authorization",jwt);
        PrintWriter out = httpServletResponse.getWriter();
//        String s = "{\"status\":\"success\",\"msg\":"+ "登录成功}";
        RestResult result = new RestResult(true, ReturnCode.SystemCode.SYS_SUCCESS.getValue(), "", null);
        out.write(JSON.toJSONString(result));
        out.flush();
        out.close();
    }
}
