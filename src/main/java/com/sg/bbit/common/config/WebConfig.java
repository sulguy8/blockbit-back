package com.sg.bbit.common.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
public class WebConfig {
	@Value("${web.cors.allowed-origins}")
	private String[] allowedOrigins;
	@Value("${web.cors.allowed-methods}")
	private String[] allowedMethods;
	@Value("${web.cors.allowed-headers}")
	private String[] allowedHeaders;
	@Value("${web.cors.url-pattern}")
	private String urlPatterns;

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration ccf = new CorsConfiguration();
		ccf.setAllowCredentials(true);
		ccf.setAllowedOrigins(List.of(allowedOrigins));
		ccf.setAllowedMethods(List.of(allowedMethods));
		ccf.setAllowedHeaders(List.of(allowedHeaders));
		
		UrlBasedCorsConfigurationSource ubccs = new UrlBasedCorsConfigurationSource();
		ubccs.registerCorsConfiguration(urlPatterns, ccf);
		return ubccs;
	}
}
