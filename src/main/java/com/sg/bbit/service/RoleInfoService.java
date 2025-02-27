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
import com.sg.bbit.mapper.RoleInfoMapper;
import com.sg.bbit.vo.RoleInfoVO;

import jakarta.annotation.Resource;

@Service
public class RoleInfoService {
	
	@Resource
	private RoleInfoMapper roiMapper;
	
	private CommonFileUtil fu;
	
	private String path = "roi";
	
	public Page<RoleInfoVO> selectROIList(RoleInfoVO roi, PaginationVO page) {
		if(page.getPageFlag()) {
			PageHelper.startPage(page.getPageNum(), page.getPageSize());
		}
		return roiMapper.selectROIList(roi);
	}
	
	public RoleInfoVO selectROI(int roiNum) {
		return roiMapper.selectROI(roiNum);
	}
		
	public Map<String, Object> updateROI(RoleInfoVO roi) {
		Map<String, Object> rMap = new HashMap<>();
		int rCnt = roiMapper.updateROI(roi);
		if(rCnt != 1) {
			throw new BizException("DB 등록 중 문제가 발생하였습니다. 다시 시도해주시기 바랍니다.");
		}
		rMap.put("cnt", rCnt);
		rMap.put("result","ok");
		return rMap;
	}
	
	public Map<String, Object> insertROI(RoleInfoVO roi) {
		Map<String, Object> rMap = new HashMap<>();
		int rCnt = roiMapper.insertROI(roi);
		if(rCnt != 1) {
			throw new BizException("DB 등록 중 문제가 발생하였습니다. 다시 시도해주시기 바랍니다.");
		}
		rMap.put("cnt", rCnt);
		rMap.put("result","ok");
		return rMap;
	}
	
	public Map<String, Object> deleteROI(List<Integer> nums) {
		Map<String, Object> rMap = new HashMap<>();
		int rCnt = 0;
		for(int num : nums) {
			rCnt += roiMapper.deleteROI(num);
		}
		if(rCnt != nums.size()) {
			throw new BizException("삭제 중 문제가 발생하였습니다. 다시 시도해주시기 바랍니다.");
		}
		rMap.put("cnt", rCnt);
		rMap.put("result","ok");
		return rMap;
	}
}