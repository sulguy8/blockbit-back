package com.sg.bbit.security.filter;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sg.bbit.common.exception.BizException;
import com.sg.bbit.common.util.ResponseUtil;
import com.sg.bbit.security.provider.JWTTokenProvider;
import com.sg.bbit.security.service.AuthUserDetailsService;
import com.sg.bbit.security.vo.AuthUserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter{
	private final JWTTokenProvider jwtProvider;
	private final AuthUserDetailsService userService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = request.getHeader(HttpHeaders.AUTHORIZATION);
		if(token == null || token.isEmpty()) {
			if(request.getCookies()!=null) {
				Cookie[] cookies = request.getCookies();
				for(Cookie cookie : cookies) {
					if(HttpHeaders.AUTHORIZATION.equals(cookie.getName())) {
						token = cookie.getValue();
						break;
					}
				}
			}
		}
		if(token != null && !token.isEmpty()) {
			token = token.replace("Bearer ", "");
			try {
				if(jwtProvider.validation(token)) {
					String uiId = jwtProvider.getId(token);
					AuthUserDetails user = (AuthUserDetails)userService.loadUserByUsername(uiId);
					user.setUsiPwd(null);
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			} catch(BizException e) {
				Map<String,Object> error = new HashMap<>();
				error.put("msg", e.getMessage());
				ResponseUtil.errorJsonWrite(response, error, HttpStatus.UNAUTHORIZED);
				return;
			}
		}
		filterChain.doFilter(request, response);
	}
}
