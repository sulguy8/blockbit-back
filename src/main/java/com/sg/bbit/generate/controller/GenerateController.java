package com.sg.bbit.generate.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sg.bbit.generate.service.GenerateService;
import com.sg.bbit.generate.vo.GenerateReqVO;
import com.sg.bbit.generate.vo.GenerateResVO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GenerateController {
	private final GenerateService generateService;
	
	@PostMapping("/generate/db")
	public void createDBInfo() {
		generateService.createDBInfo();
	}
	
	@PostMapping("/generate/all")
	public GenerateResVO generateAll(@RequestBody GenerateReqVO generateReq, final GenerateResVO generateRes) {
		generateControllerCode(generateReq, generateRes);
		generateServiceCode(generateReq, generateRes);
		generateVOCode(generateReq, generateRes);
		generateMapperCode(generateReq, generateRes);
		generateMapperXML(generateReq, generateRes);
		return generateRes;
	}
	
	@PostMapping("/generate/controllerCode")
	public GenerateResVO generateControllerCode(@RequestBody GenerateReqVO generateReq, final GenerateResVO generateRes) {
		generateReq.setGenerateTarget("controllerCode");
		generateRes.setController(generateService.generateTemplate(generateReq));
		return generateRes;
	}
	
	@PostMapping("/generate/serviceCode")
	public GenerateResVO generateServiceCode(@RequestBody GenerateReqVO generateReq, final GenerateResVO generateRes) {
		generateReq.setGenerateTarget("serviceCode");
		generateRes.setService(generateService.generateTemplate(generateReq));
		return generateRes;
	}
	
	@PostMapping("/generate/voCode")
	public GenerateResVO generateVOCode(@RequestBody GenerateReqVO generateReq, final GenerateResVO generateRes) {
		generateReq.setGenerateTarget("voCode");
		generateRes.setVo(generateService.generateTemplate(generateReq));
		return generateRes;
	}
	
	@PostMapping("/generate/mapperCode")
	public GenerateResVO generateMapperCode(@RequestBody GenerateReqVO generateReq, final GenerateResVO generateRes) {
		generateReq.setGenerateTarget("mapperCode");
		generateRes.setMapper(generateService.generateTemplate(generateReq));
		return generateRes;
	}
	
	@PostMapping("/generate/mapperXml")
	public GenerateResVO generateMapperXML(@RequestBody GenerateReqVO generateReq, final GenerateResVO generateRes) {
		generateReq.setGenerateTarget("mapper-xml");
		generateRes.setMapperXML(generateService.generateTemplate(generateReq));
		return generateRes;
	}
}
