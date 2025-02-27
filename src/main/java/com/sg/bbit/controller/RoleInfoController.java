package com.sg.bbit.controller;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sg.bbit.common.vo.PaginationVO;
import com.sg.bbit.service.RoleInfoService;
import com.sg.bbit.vo.RoleInfoVO;

import jakarta.annotation.Resource;

@RestController
public class RoleInfoController {

	@Resource
	private RoleInfoService roiService;

	@GetMapping("/rois")
	public Closeable selectROIList(RoleInfoVO roi, PaginationVO page){
		return roiService.selectROIList(roi, page);
	}
	
	@GetMapping("/roi/{roiNum}")
	public RoleInfoVO selectROI(@PathVariable("roiNum") int roiNum){
		return roiService.selectROI(roiNum);
	}
	
	@PostMapping("/roi")
	public Map<String, Object> insertROI(@RequestBody RoleInfoVO roi) {
		return roiService.insertROI(roi);
	}
	
	@PostMapping("/roi/mod")
	public Map<String, Object> updateROI(@RequestBody RoleInfoVO roi) {
		return roiService.updateROI(roi);
	}
	
	@PostMapping("/roi/del")
	public Map<String, Object> deleteROI(@RequestBody List<Integer> nums) {
		return roiService.deleteROI(nums);
	}
}
