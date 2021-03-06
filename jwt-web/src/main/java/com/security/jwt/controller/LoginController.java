package com.security.jwt.controller;

import com.alibaba.fastjson.JSON;
import com.security.jwt.common_const.CommonConst;
import com.security.jwt.dto.LoginInfo;
import com.security.jwt.po.UserPo;
import com.security.jwt.redis.RedisConstants;
import com.security.jwt.redis.RedisUtil;
import com.security.jwt.service.IUserService;
import com.security.jwt.service.impl.security.component.UserDetailsServiceImpl;
import com.wd.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

/**
 * @author miou
 * @date 2019-04-13
 */
@RestController
@RequestMapping(value = "user")
public class LoginController {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    private final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    IUserService userService;
    @Autowired
    RedisUtil redisUtil;

    /**
     * 登录
     * @param userName
     * @param password
     * @return
     */
    @PostMapping(value = "v1/userLogin")
//    @NoRestReturn
    public LoginInfo login(@RequestParam("userName") String userName,
                           @RequestParam("password") String password){
        LoginInfo result=null;
        HashSet<String> keySet;
        String key=null;
        UserPo userPo = new UserPo();
        userPo.setUserName(userName);
        userPo.setPassword(password);
        UserPo rtUser = userService.verifyUser(userPo);
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setUserName(userName);
        loginInfo.setLoginTime(DateUtil.getDateTime());
        keySet = redisUtil.getKeys(String.format(RedisConstants.LOGIN_INFO,userName,"*"));
        Iterator iterator = keySet.iterator();
        while (iterator.hasNext()) {
            key = String.valueOf(iterator.next());
        }
        //如果用户未登录
        if (rtUser.getIsLogin().equals("0")){
            loginInfo.setToken(UUID.randomUUID().toString());
            rtUser.setIsLogin("1");
            userService.updateUser(rtUser);
            //记录用户登录信息
            redisUtil.set(String.format(RedisConstants.LOGIN_INFO,userName,loginInfo.getToken()),JSON.toJSONString(loginInfo),
                    CommonConst.LOGININFO_EXPIRED);
            result = loginInfo;
        }else if (rtUser.getIsLogin().equals("1")){//如果已经登录过
            if (key != null && redisUtil.hasKey(key)){
                redisUtil.del(key);
            }
            loginInfo.setToken(UUID.randomUUID().toString());
            userService.updateUser(rtUser);
            redisUtil.set(String.format(RedisConstants.LOGIN_INFO,userName,loginInfo.getToken()),JSON.toJSONString(loginInfo),
                    CommonConst.LOGININFO_EXPIRED);
            result = loginInfo;
        }
        return result;
    }

    /**
     * 注册
     * @param userName
     * @param password
     * @return
     */
    @PostMapping(value = "v1/register")
    public void registerUser(@RequestParam("userName") String userName,
                             @RequestParam("password") String password){
        userService.registerUser(userName,password);
    }

    /**
     * 注销
     * @return
     */
    @PostMapping(value = "logout")
    public void logout(){
    }
}
