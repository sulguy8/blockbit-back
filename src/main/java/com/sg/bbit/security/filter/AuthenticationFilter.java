package com.sg.bbit.security.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg.bbit.common.util.ResponseUtil;
import com.sg.bbit.security.provider.JWTTokenProvider;
import com.sg.bbit.security.vo.AuthUserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final JWTTokenProvider jwt;
	private final ObjectMapper om;
	public AuthenticationFilter(AuthenticationManager manager, JWTTokenProvider jwt, ObjectMapper om) {
		super(manager);
		this.jwt = jwt;
		this.om = om;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		if (!request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}
		try {
			AuthUserDetails user = om.readValue(request.getInputStream(), AuthUserDetails.class);
			UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(user.getUsiId(), user.getUsiPwd());
			setDetails(request, authRequest);
			return this.getAuthenticationManager().authenticate(authRequest);
		}catch(Exception e) {
			throw new AuthenticationServiceException("Login error");
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		AuthUserDetails user = (AuthUserDetails)authResult.getPrincipal();
		user.setUsiPwd(null);
		String token = jwt.generateToken(authResult);
		user.setRefreshToken(jwt.generateRefreshToken(authResult));
		ResponseCookie resCookie = jwt.getJwtReponseCookie(token);
		response.addHeader(HttpHeaders.SET_COOKIE, resCookie.toString());
		ResponseUtil.jsonWrite(response, om.writeValueAsString(user));
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		Map<String,Object> error = new HashMap<>(); 
		error.put("msg", "Login fail");
		ResponseUtil.errorJsonWrite(response, error, HttpStatus.UNAUTHORIZED);
	}
}
