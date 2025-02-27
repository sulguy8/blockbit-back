package com.sg.bbit.security.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg.bbit.common.util.ResponseUtil;
import com.sg.bbit.security.provider.JWTTokenProvider;
import com.sg.bbit.security.vo.AuthUserDetails;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityService {
	private final ObjectMapper om;
	private final JWTTokenProvider jwtProvider;
	private final StringRedisTemplate redisTemplate;

	public void refreshToken(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException {
		String refreshToken = request.getHeader(HttpHeaders.AUTHORIZATION);
		Map<String,Object> error = new HashMap<>();
		if(refreshToken != null) {
			refreshToken = refreshToken.replace("Bearer ", "");
			Boolean isStoredRefreshToken = refreshToken.equals(redisTemplate.opsForValue().get(jwtProvider.getId(refreshToken)));
			if(isStoredRefreshToken) {
				AuthUserDetails user = (AuthUserDetails)authResult.getPrincipal();
				String token = jwtProvider.generateToken(authResult);
				ResponseCookie resCookie = jwtProvider.getJwtReponseCookie(token);
				response.addHeader(HttpHeaders.SET_COOKIE, resCookie.toString());
				ResponseUtil.jsonWrite(response, om.writeValueAsString(user));
			} else {
				error.put("msg", "Wrong refresh token");
				ResponseUtil.errorJsonWrite(response, error, HttpStatus.UNAUTHORIZED);
			}
		} else {
			error.put("msg", "Refresh token not provided");
			ResponseUtil.errorJsonWrite(response, error, HttpStatus.UNAUTHORIZED);
		}
	}
}
