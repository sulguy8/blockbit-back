package com.sg.bbit.mapper;

import org.mybatis.spring.annotation.MapperScan;

import com.github.pagehelper.Page;
import com.sg.bbit.vo.UserInfoVO;

@MapperScan
public interface UserInfoMapper {
	Page<UserInfoVO> selectUSIList(UserInfoVO usi);
	UserInfoVO selectUSI(Integer usiNum);
	Integer updateUSI(UserInfoVO usi);
	Integer insertUSI(UserInfoVO usi);
	Integer deleteUSI(Integer usiNum);
}