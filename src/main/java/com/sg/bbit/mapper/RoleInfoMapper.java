package com.sg.bbit.mapper;

import org.mybatis.spring.annotation.MapperScan;

import com.github.pagehelper.Page;
import com.sg.bbit.vo.RoleInfoVO;

@MapperScan
public interface RoleInfoMapper {
	Page<RoleInfoVO> selectROIList(RoleInfoVO roi);
	RoleInfoVO selectROI(Integer roiNum);
	Integer updateROI(RoleInfoVO roi);
	Integer insertROI(RoleInfoVO roi);
	Integer deleteROI(Integer roiNum);
}