package com.security.jwt.service.impl.security.handler;

import com.security.jwt.service.impl.security.component.UserDetailsServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenClearLogoutHandler implements LogoutHandler {

	private UserDetailsServiceImpl userDetailsService;
	
	public TokenClearLogoutHandler(UserDetailsServiceImpl userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		clearToken(authentication);
	}
	
	protected void clearToken(Authentication authentication) {
		if(authentication == null)
			return;
		UserDetails user = (UserDetails)authentication.getPrincipal();
		if(user!=null && user.getUsername()!=null)
			userDetailsService.deleteUserLoginInfo(user.getUsername());
	}

}
