package com.order.machine.service.impl;

import com.order.machine.common_const.CommonEnum;
import com.order.machine.exception.LogicException;
import com.order.machine.mapper.UserMapper;
import com.order.machine.po.UserPo;
import com.order.machine.service.IUserService;
import com.wd.encrypt.MD5Utils;
import com.wd.util.VertifyCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.UUID;

/**
 * @author miou
 * @date 2019-04-16
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public boolean verifyUser(UserPo userPo){
        Example example = new Example(UserPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userPo.getUserId());
        UserPo user = userMapper.selectOneByExample(example);
        if (null == user){
            throw LogicException.le(CommonEnum.ReturnCode.UserLoginCode.user_login_UserNotExists.getValue(),
                    "该用户不存在");
        }
//        String pwd = MD5Utils.getMD5(userPo.getPassword() + user.getSalt());
        String pwd = new BCryptPasswordEncoder().encode(userPo.getPassword() + user.getSalt());
        if (!pwd.equals(user.getPassword())){
            throw LogicException.le(CommonEnum.ReturnCode.UserLoginCode.user_password_error.getValue(),
                    "密码错误");
        }
        return true;
    }

    @Override
    public void registerUser(String userId,String userName,String password){
        Example example = new Example(UserPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("userName",userName);
        if (userMapper.selectCountByExample(example) > 0){
            throw LogicException.le(CommonEnum.ReturnCode.UserLoginCode.user_already_exists.getValue(),
                    "用户id已存在");
        }
        String salt = VertifyCodeUtil.getRandromNum();
//        String pwd = MD5Utils.getMD5(password + salt);
        String pwd = new BCryptPasswordEncoder().encode(password+salt); //+ salt
        String id = UUID.randomUUID().toString();
        UserPo user = new UserPo();
        user.setId(id);
        user.setUserId(userId);
        user.setUserName(userName);
        user.setPassword(pwd);
        user.setSalt(salt);
        userMapper.insert(user);
    }


}
