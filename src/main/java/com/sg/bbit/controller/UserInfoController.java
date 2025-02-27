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
import com.sg.bbit.service.UserInfoService;
import com.sg.bbit.vo.UserInfoVO;

import jakarta.annotation.Resource;

@RestController
public class UserInfoController {

	@Resource
	private UserInfoService usiService;

	@GetMapping("/usis")
	public Closeable selectUSIList(UserInfoVO usi, PaginationVO page){
		return usiService.selectUSIList(usi, page);
	}
	
	@GetMapping("/usi/{usiNum}")
	public UserInfoVO selectUSI(@PathVariable("usiNum") int usiNum){
		return usiService.selectUSI(usiNum);
	}
	
	@PostMapping("/usi")
	public Map<String, Object> insertUSI(@RequestBody UserInfoVO usi) {
		return usiService.insertUSI(usi);
	}
	
	@PostMapping("/usi/mod")
	public Map<String, Object> updateUSI(@RequestBody UserInfoVO usi) {
		return usiService.updateUSI(usi);
	}
	
	@PostMapping("/usi/del")
	public Map<String, Object> deleteUSI(@RequestBody List<Integer> nums) {
		return usiService.deleteUSI(nums);
	}
}
