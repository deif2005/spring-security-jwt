package com.order.machine.service.impl.security;

import com.order.machine.po.RolePo;
import com.order.machine.po.UserPo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author miou
 * @date 2019-05-08
 */
@Service
public class UserDetailsImpl implements UserDetails {

    private String userName;
    private String password;
    //包含着用户对应的所有Role，在使用时调用者给对象注入roles
    private List<RolePo> roles;

    public UserDetailsImpl(){
    }

    //用User构造
    public UserDetailsImpl(UserPo user) {
        this.userName = user.getUserName();
        this.password = user.getPassword();
    }

    //用User和List<Role>构造
    public UserDetailsImpl(UserPo user, List<RolePo> roles) {
        this.userName = user.getUserName();
        this.password = user.getPassword();
        this.roles = roles;
    }

    /**
     * 生成授权信息GrantedAuthrity
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for(RolePo role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    public void setUsername(String username) {
        this.userName = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<RolePo> getRoles() {
        return roles;
    }

    public void setRoles(List<RolePo> roles) {
        this.roles = roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
