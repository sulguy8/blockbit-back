package com.sg.bbit.security.provider;

import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.sg.bbit.common.exception.BizException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTTokenProvider {
	private final StringRedisTemplate redisTemplate;

	@Value("${security.token-secret}")
	private String tokenSecret;

	@Value("${security.token-expire}")
	private int tokenExpire;

	@Value("${security.refresh-token-expire}")
	private int refreshTokenExpire;
	
	public int getTokenExpire() {
		return tokenExpire;
	}

	private Date getJwtExpiretime(int tokenExpire) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MILLISECOND, tokenExpire);
		return c.getTime();
	}
	
	private Key getKey() {
		byte[] bytes = DatatypeConverter.parseBase64Binary(tokenSecret);
		return new SecretKeySpec(bytes, SignatureAlgorithm.HS256.getJcaName());
	}
	
	private String getJwt(Map<String,Object> claims, Date now, Date expireDate) {
		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(expireDate)
				.signWith(SignatureAlgorithm.HS256, getKey())
				.compact();
	}
	
	public ResponseCookie getJwtReponseCookie(String token) {
		return ResponseCookie
				.from(HttpHeaders.AUTHORIZATION, token)
				.httpOnly(true)
				.sameSite("None")
				.secure(true)
				.path("/")
				.maxAge(getTokenExpire())
				.build();
	}
	
	public String generateToken(Authentication authentication) {
		Claims claims = Jwts.claims().setSubject(authentication.getName());
		return getJwt(claims,Calendar.getInstance().getTime(), getJwtExpiretime(tokenExpire));
	}

	public String generateRefreshToken(Authentication authentication) {
		Claims claims = Jwts.claims().setSubject(authentication.getName());
		Date expireTime = getJwtExpiretime(refreshTokenExpire);
		String refreshToken = getJwt(claims,Calendar.getInstance().getTime(), expireTime);
		redisTemplate.opsForValue().set(authentication.getName(), refreshToken, refreshTokenExpire, TimeUnit.MILLISECONDS);
		return refreshToken;
	}

	public boolean validation(String token) {
		try {
			Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(token);
			return true;
		} catch(ExpiredJwtException e) {
			throw new BizException("Expired JWT token");
		} catch(JwtException e) {
			throw new BizException("Invalid JWT token");
		} catch(IllegalArgumentException e) {
			throw new BizException("JWT claims string is empty");
		}
	}

	public String getId(String token) {
		Claims claims = Jwts.parser().setSigningKey(getKey()).parseClaimsJws(token).getBody();
		return claims.get("sub").toString();
	}
}
