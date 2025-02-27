package com.sg.bbit.security.mapper;

import java.util.List;

import org.mybatis.spring.annotation.MapperScan;

import com.sg.bbit.security.vo.AuthUserDetails;
import com.sg.bbit.security.vo.AuthUserRole;

@MapperScan
public interface AuthUserMapper {
	AuthUserDetails authUserDetails(String usiId);
	List<AuthUserRole> authUserRole(Integer usiNum);
}