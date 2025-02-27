package com.sg.bbit.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg.bbit.security.filter.AuthenticationFilter;
import com.sg.bbit.security.filter.AuthorizationFilter;
import com.sg.bbit.security.provider.CustomAuthenticationProvider;
import com.sg.bbit.security.provider.JWTTokenProvider;
import com.sg.bbit.security.service.AuthUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	@Value("${security.ignores}")
	private String[] ignores;
	@Value("${security.permit-all}")
	private String[] permitAlls;
	@Value("${security.login-process}")
	private String loginProcess;
	
	private final PasswordEncoder passwordEncoder;
	private final AuthUserDetailsService userDetailsService;
	private final ObjectMapper objectMapper;
	private final JWTTokenProvider jwtProvider;

	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		return (web)->{
			web.ignoring()
			.requestMatchers(ignores)
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
		};
	}

	@Bean
	UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter() {
		AuthenticationFilter filter = new AuthenticationFilter(authenticationManager(),jwtProvider,objectMapper);
		filter.setFilterProcessesUrl(loginProcess);
		filter.afterPropertiesSet();
		return filter;
	}
	@Bean
	AuthenticationProvider authenticationProvider() {
		return new CustomAuthenticationProvider(userDetailsService, passwordEncoder);
	}
	@Bean
	AuthenticationManager authenticationManager() {
		return new ProviderManager(authenticationProvider());
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity hs) throws Exception {
		hs.csrf(csrf->csrf.disable());
		hs.cors(Customizer.withDefaults());
		hs.authorizeHttpRequests(auth -> {
			auth
				.requestMatchers(permitAlls).permitAll()
				.requestMatchers("/user").hasAnyRole("USER", "MANAGER", "ADMIN")
				.requestMatchers("/manager").hasAnyRole("MANAGER", "ADMIN")
				.requestMatchers("/admin").hasAnyRole("ADMIN")
				.anyRequest().authenticated();
		});
		hs.formLogin(login -> login.disable());
		hs.logout(logout -> 
			logout
				.logoutUrl("/auth/logout")
				.logoutSuccessUrl("/html/auth/login")
		);
		hs.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		hs.addFilterBefore(usernamePasswordAuthenticationFilter(),UsernamePasswordAuthenticationFilter.class);
		hs.addFilterBefore(new AuthorizationFilter(jwtProvider,userDetailsService),UsernamePasswordAuthenticationFilter.class);
		return hs.build();
	}
}
