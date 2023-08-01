package com.conneqt.config;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.conneqt.controller.LoginController;
import com.conneqt.model.User;
import com.conneqt.repository.UserRepository;
import com.conneqt.service.DefaultUserService;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

	@Autowired
	UserRepository userRepo;
	
	@Autowired
	DefaultUserService userService;
	
	
	@Autowired
	LoginController login;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		String redirectUrl = null;

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = userRepo.findByEmail(username);
        String output = login.generateOtp(user);
        if(output.equals("success")) {
			redirectUrl="/login/otpVerification";
        }
		new DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);
	}
}