package com.sg.bbit.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

@Configuration
public class LocaleConfig implements WebMvcConfigurer {
	@Bean
	public LocaleResolver localeResolver() {
		CookieLocaleResolver resolver = new CookieLocaleResolver("Lang");
		resolver.setDefaultLocale(Locale.getDefault());
		return resolver;
	}

	/**
	 * Locale 값이 변경되면 인터셉터가 동작한다.(언어 변경을 위한 인터셉터를 생성한다.)
	 * url의 query parameter에 지정한 값이 들어올 때 동작한다.
	 * ex) http://localhost:8080?lang=ko
	 * */
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName("lang");
		return interceptor;
	}
	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:i18n/messages"); // 메세지 프로퍼티파일의 위치와 이름을 지정한다.
		messageSource.setDefaultEncoding("UTF-8"); // 기본 인코딩을 지정한다.
		messageSource.setCacheSeconds(60); // 캐시 유효 시간 (초) 프로퍼티 파일의 변경을 감지할 시간 간격을 지정한다. 리로드 시간
		messageSource.setUseCodeAsDefaultMessage(true); // 없는 메세지일 경우 예외를 발생시키는 대신 코드를 기본 메세지로 한다.
		return messageSource;
	}

	/**
	 * 인터셉터 등록
	 * LocaleChangeInterceptor 를 스프링 컨테이너에 등록한다.
	 * WebMvcConfigurer 를 상속받고 addInterceptors를 오버라이딩 한다.
	 * */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}
}