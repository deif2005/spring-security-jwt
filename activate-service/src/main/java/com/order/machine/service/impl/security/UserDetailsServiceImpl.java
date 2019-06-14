package com.order.machine.service.impl.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.order.machine.mapper.RoleMapper;
import com.order.machine.mapper.UserMapper;
import com.order.machine.mapper.UserRoleMapper;
import com.order.machine.po.UserPo;
import com.order.machine.po.UserRolePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author miou
 * @date 2019-05-08
 * 框架需要使用到一个实现了UserDetailsService接口的类
 * 验证用户是否存在，以及获取该用户角色
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    RoleMapper roleMapper;

    @Transactional
    public List<UserPo> getByUserName(String userName)
    {
        UserPo userPo = new UserPo();
        userPo.setUserName(userName);
        return userMapper.select(userPo);
    }

    /**
     * 根据用户名获取用户信息，封装成UserDetails，提供给验证provider使用
     * @param username
     * @return
     */
    public UserDetails getUserLoginInfo(String username) {
        String salt = "123456ef";
        /**
         * @todo 从数据库或者缓存中取出jwt token生成时用的salt
         * salt = redisTemplate.opsForValue().get("token:"+username);
         */
        UserDetails user = loadUserByUsername(username);
        //将salt放到password字段返回
        return User.builder().username(user.getUsername()).password(salt).authorities(user.getAuthorities()).build();
    }

    /**
     * 保存验证信息，salt设置有效时间，如果超过时间则无法使用salt来验证客户端传过来的token值
     * @param user
     * @return
     */
    public String saveUserLoginInfo(UserDetails user) {
        String salt = "123456ef";
        //正式开发时可以调用该方法实时生成加密的salt
//        BCrypt.gensalt();
        /**
         * @todo 将salt保存到数据库或者缓存中
         * redisTemplate.opsForValue().set("token:"+username, salt, 3600, TimeUnit.SECONDS);
         */
        Algorithm algorithm = Algorithm.HMAC256(salt);
        Date date = new Date(System.currentTimeMillis()+3600*1000);  //设置1小时后过期
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(date)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    /**
     * 根据用户名获取用户信息，包括权限信息
     * @param s
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        List<UserPo> userList = getByUserName(s);
        UserPo user = null;
        if (userList != null && userList.size() > 0)
            user = userList.get(0);
        if(user == null)
        {
            throw new UsernameNotFoundException("没有该用户");
        }
        //查到User后将其封装为UserDetails的实现类的实例供程序调用
        //用该User和它对应的Role实体们构造UserDetails的实现类
        return new UserDetailsImpl(user,roleMapper.getRoleByUserName(user.getUserName()));
    }

    public void deleteUserLoginInfo(String username) {
        /**
         * @todo 清除数据库或者缓存中登录salt
         */
    }
}
