package com.order.machine.controller;

import com.alibaba.fastjson.JSON;
import com.order.machine.dto.LoginInfo;
import com.order.machine.jwt.JwtUtil;
import com.order.machine.jwt.UserLoginToken;
import com.order.machine.po.UserPo;
import com.order.machine.redis.RedisConstants;
import com.order.machine.redis.RedisUtil;
import com.order.machine.service.IUserService;
import com.wd.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @author miou
 * @date 2019-04-13
 */
@RestController
public class LoginController {

    private final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    IUserService userService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    JwtUtil jwtUtil;

    @PostMapping(value = "/user/login")
    public String login(@RequestParam("username") String userName,
                        @RequestParam("password") String password){
        String result="";
        UserPo userPo = new UserPo();
        userPo.setUserName(userName);
        userPo.setPassword(password);
        if (userService.verifyUser(userPo)){
            if (!redisUtil.hasKey(String.format(RedisConstants.LOGIN_TOKEN,userName))){
                LoginInfo loginInfo = new LoginInfo();
                loginInfo.setUserName(userName);
                loginInfo.setToken(jwtUtil.getToken(userPo));
                loginInfo.setLoginTime(DateUtil.getDateTime());
                String loginStr = JSON.toJSONString(loginInfo);
                //记录用户是否登录，解决重复登录问题
                redisUtil.set(String.format(RedisConstants.LOGIN_TOKEN,userName),loginInfo.getToken());
                //记录用户登录信息
                redisUtil.set(String.format(RedisConstants.LOGIN_INFO,loginInfo.getToken()),loginStr);
                result = loginInfo.getToken();
            }else {//如果已经登录过，直接返回
                result = String.valueOf(redisUtil.get(String.format(RedisConstants.LOGIN_TOKEN,userName)));
            }
        }
        return result;
    }

    @PostMapping(value = "register")
    public String registerUser(@RequestParam("userId") String userId,
                               @RequestParam("userName") String userName,
                               @RequestParam("password") String password){
        userService.registerUser(userId,userName,password);
        return "";
    }

    @UserLoginToken
    @PostMapping(value = "logout")
    public String logout(HttpServletRequest request){
        String token = request.getHeader("token");
        LoginInfo loginInfo = JSON.parseObject((String) redisUtil.get(String.format(RedisConstants.LOGIN_INFO,token)),
                LoginInfo.class) ;
        String userId = loginInfo.getUserId();
        redisUtil.del(String.format(RedisConstants.LOGIN_TOKEN,userId));
        redisUtil.del(String.format(RedisConstants.LOGIN_INFO,token));
        return "注销成功";
    }
}
