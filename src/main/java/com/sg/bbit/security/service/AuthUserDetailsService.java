package com.sg.bbit.security.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sg.bbit.security.mapper.AuthUserMapper;
import com.sg.bbit.security.vo.AuthUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {
	private final AuthUserMapper userMapper;
	
	@Override
	public AuthUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AuthUserDetails user = userMapper.authUserDetails(username);
		if(user == null) {
			throw new UsernameNotFoundException("아이디 비밀번호 확인!!");
		} else {
			user.setRoles(userMapper.authUserRole(user.getUsiNum()));
			return user;
		}
	}
}
