package com.order.machine.service.impl.security.component;

import com.order.machine.common_const.CommonEnum;
import com.order.machine.exception.LogicException;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author miou
 * @date 2019-05-08
 * 负责校验当前用户是否具备访问权限
 */
@Component
public class AccessDecisionManagerImpl implements AccessDecisionManager {

    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws
            AccessDeniedException, InsufficientAuthenticationException {
        //迭代器遍历目标url的权限列表
        Iterator<ConfigAttribute> iterator = collection.iterator();
        while (iterator.hasNext()) {
            ConfigAttribute ca = iterator.next();
            String needRole = ca.getAttribute();
            if ("ROLE_LOGIN".equals(needRole)) {
                if (authentication instanceof AnonymousAuthenticationToken) {
                    throw new BadCredentialsException("未登录");
                } else
                    return;
            } else if ("ROLE_ANONYMOUS".equals(needRole)){
                if (authentication instanceof AnonymousAuthenticationToken) {
                    return;
                }
            }
            //遍历当前用户所具有的权限
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(needRole)) {
                    return;
                }
            }
        }
//        LogicException.le(CommonEnum.ReturnCode.SystemCode.sys_err_noauth.getValue(),
//                CommonEnum.ReturnMsg.SystemMsg.sys_err_noauth.getValue());
        //执行到这里说明没有匹配到应有权限
        throw new AccessDeniedException("权限不足!");
//        throw new InsufficientAuthenticationException("权限不足");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return false;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }
}
