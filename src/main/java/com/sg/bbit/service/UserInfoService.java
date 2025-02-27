package com.sg.bbit.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sg.bbit.common.exception.BizException;
import com.sg.bbit.common.util.CommonFileUtil;
import com.sg.bbit.common.vo.PaginationVO;
import com.sg.bbit.mapper.UserInfoMapper;
import com.sg.bbit.vo.UserInfoVO;

import jakarta.annotation.Resource;

@Service
public class UserInfoService {

	@Resource
	private UserInfoMapper usiMapper;
	
	private CommonFileUtil fu;
	
	private String path = "usi";
	
	public Page<UserInfoVO> selectUSIList(UserInfoVO usi, PaginationVO page) {
		if(page.getPageFlag()) {
			PageHelper.startPage(page.getPageNum(), page.getPageSize());
		}
		return usiMapper.selectUSIList(usi);
	}
	
	public UserInfoVO selectUSI(int usiNum) {
		return usiMapper.selectUSI(usiNum);
	}
		
	public Map<String, Object> updateUSI(UserInfoVO usi) {
		Map<String, Object> rMap = new HashMap<>();
		int rCnt = usiMapper.updateUSI(usi);
		if(rCnt != 1) {
			throw new BizException("DB 등록 중 문제가 발생하였습니다. 다시 시도해주시기 바랍니다.");
		}
		rMap.put("cnt", rCnt);
		rMap.put("result","ok");
		return rMap;
	}
	
	public Map<String, Object> insertUSI(UserInfoVO usi) {
		Map<String, Object> rMap = new HashMap<>();
		int rCnt = usiMapper.insertUSI(usi);
		if(rCnt != 1) {
			throw new BizException("DB 등록 중 문제가 발생하였습니다. 다시 시도해주시기 바랍니다.");
		}
		rMap.put("cnt", rCnt);
		rMap.put("result","ok");
		return rMap;
	}
	
	public Map<String, Object> deleteUSI(List<Integer> nums) {
		Map<String, Object> rMap = new HashMap<>();
		int rCnt = 0;
		for(int num : nums) {
			rCnt += usiMapper.deleteUSI(num);
		}
		if(rCnt != nums.size()) {
			throw new BizException("삭제 중 문제가 발생하였습니다. 다시 시도해주시기 바랍니다.");
		}
		rMap.put("cnt", rCnt);
		rMap.put("result","ok");
		return rMap;
	}
}