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
    public void updateUser(UserPo userPo){
        Example example = new Example(UserPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userName",userPo.getUserName());
        userMapper.updateByExampleSelective(userPo,example);
    }

    /**
     * 验证用户信息
     * @param userPo
     * @return
     */
    @Override
    public UserPo verifyUser(UserPo userPo){
        Example example = new Example(UserPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userName",userPo.getUserName());
        UserPo user = userMapper.selectOneByExample(example);
        if (null == user){
            throw LogicException.le(CommonEnum.ReturnCode.UserLoginCode.user_login_userorpassword_error.getValue(),
                    "该用户不存在");
        }
        String pw = MD5Utils.getMD5(userPo.getPassword() + user.getSalt());
        if (!pw.equals(user.getPassword())){
            throw LogicException.le(CommonEnum.ReturnCode.UserLoginCode.user_password_error.getValue(),
                    "密码错误");
        }
        return user;
    }

    /**
     * 用户注册
     * @param userName
     * @param password
     */
    @Override
    public void registerUser(String userName,String password) {
        Example example = new Example(UserPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userName",userName);
        if (userMapper.selectCountByExample(example) > 0){
            throw LogicException.le(CommonEnum.ReturnCode.UserLoginCode.user_already_exists.getValue(),
                    "用户名已存在");
        }
        String salt = VertifyCodeUtil.getRandromNum();
        String pwd = new BCryptPasswordEncoder().encode(password); //+ salt
        UserPo user = new UserPo();
        user.setUserName(userName);
        user.setPassword(pwd);
        user.setSalt(salt);
        userMapper.insertSelective(user);

    }
}
