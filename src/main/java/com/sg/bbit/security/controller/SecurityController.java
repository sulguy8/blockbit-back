package com.sg.bbit.security.controller;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sg.bbit.common.exception.BizException;
import com.sg.bbit.security.provider.JWTTokenProvider;
import com.sg.bbit.security.service.SecurityService;
import com.sg.bbit.security.vo.AuthUserDetails;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SecurityController {
	private final SecurityService securityService;
	private final JWTTokenProvider jwtTokenProvider;
	
	@PostMapping("/refresh-token")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException {
		securityService.refreshToken(request, response, authResult);
	}
	
	@GetMapping("/auth/check")
	public Boolean validateToken(HttpServletRequest request) {
		try {
			Cookie[] cookies = request.getCookies();
			String token = null;
			
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if ("Authorization".equals(cookie.getName())) {
						token = cookie.getValue();
						break;
					}
				}
			}
			
			return jwtTokenProvider.validation(token);
		} catch (BizException e) {
			return false;
		}
	}
	
	@GetMapping("home")
	public String home() {
		return "<h1>home</h1>";
	}
	
	@GetMapping("user")
	public String user(Authentication authentication) {
		AuthUserDetails principal = (AuthUserDetails) authentication.getPrincipal();
		return "user";
	}

	@GetMapping("manager")
	public String manager() {
		return "manager";
	}

	@GetMapping("admin")
	public String admin(){
		return "admin";
	}
}
