package com.sg.bbit.generate.service;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg.bbit.common.exception.BizException;
import com.sg.bbit.generate.mapper.GenerateMapper;
import com.sg.bbit.generate.vo.GenerateReqVO;
import com.sg.bbit.generate.vo.GenerateVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GenerateService {
	@Value("${generate.default-package}")
	private String defaultPackagePath;
	@Value("${velocity.template.controller-name}")
	private String templateControllerName;
	@Value("${velocity.template.service-name}")
	private String templateServiceName;
	@Value("${velocity.template.vo-name}")
	private String templateVoName;
	@Value("${velocity.template.mapper-name}")
	private String templateMapperName;
	@Value("${velocity.template.mapper-xml-name}")
	private String templateMapperXmlName;
	
	private final ObjectMapper objectMapper;
	private final GenerateMapper generateMapper;
	private final VelocityService velocityService;
	private boolean reload = false;
	
	public void createDBInfo() {
		generateMapper.createDBInfo();
	}

	public String generateTemplate(final GenerateReqVO genConfig) {
		genConfig.setPackageName(defaultPackagePath);
		genConfig.setTableCamelName(setTableName(genConfig.getTableName(), "camel"));
		genConfig.setTablePascalName(setTableName(genConfig.getTableName(), "pascal"));
		
		String fileName = "";
		String templateName = "";
		String fileExtension = "";
		switch(genConfig.getGenerateTarget()) {
			case "controllerCode" :
				genConfig.setControllerName(genConfig.getTablePascalName() + "Controller");
				fileName = genConfig.getControllerName(); 
				templateName = templateControllerName;
				fileExtension = ".java";
				break;
			case "serviceCode" :
				genConfig.setServiceName(genConfig.getTablePascalName() + "Service");
				fileName = genConfig.getServiceName();
				templateName = templateServiceName;
				fileExtension = ".java";
				break;
			case "voCode" :
				genConfig.setVoName(genConfig.getTablePascalName() + "VO");
				fileName = genConfig.getVoName();
				templateName = templateVoName;
				fileExtension = ".java";
				break;
			case "mapperCode" :
				genConfig.setMapperName(genConfig.getTablePascalName() + "Mapper");
				fileName = genConfig.getMapperName();
				templateName = templateMapperName;
				fileExtension = ".java";
				break;
			case "mapper-xml" :
				genConfig.setMapperXmlName(genConfig.getTablePascalName() + "Mapper");
				fileName = genConfig.getMapperXmlName();
				templateName = templateMapperXmlName;
				fileExtension = ".xml";
				break;
		}
		String template = velocityService.getTemplate(templateName, setVelocityContext(genConfig));
		createFile("", fileName + fileExtension, template, null);
		return template;
	}
	
	public VelocityContext setVelocityContext(GenerateReqVO genConfig) {
		GenerateReqVO genReq = new GenerateReqVO();
		
		genReq.setTableName(genConfig.getTableName());
		GenerateVO tableInfo = generateMapper.selectDTI(genReq);
		if(tableInfo == null) throw new BizException("DB_TABLE_INFO : 테이블 명을 확인해주세요.");
		if(tableInfo.getTableAlias() == null) throw new BizException("DB_TABLE_INFO : 해당 테이블의 별칭(DTI_ALIAS)이 설정되어 있지 않습니다.");
		
		genReq.setTableNum(tableInfo.getTableNum());
		GenerateVO pageInfo = generateMapper.selectPAI(genReq);
		
		List<GenerateVO> genColumns = generateMapper.selectDCIList(genReq);
		List<GenerateVO> genFkColumns = new ArrayList<GenerateVO>();
		
		Set<Map<String, String>> fkTableInfo = new HashSet<Map<String, String>>();
		if(pageInfo != null && pageInfo.getFkColumnMapp() != null) {
			try {
				HashMap<String, String> fkTable = objectMapper.readValue(pageInfo.getFkColumnMapp(), HashMap.class);
				for(String fkAlias : fkTable.keySet()) {
					GenerateReqVO fkGenConfig = new GenerateReqVO();
					fkGenConfig.setTableAlias(fkAlias.toLowerCase());
					for(GenerateVO fkCol : generateMapper.selectDCIList(fkGenConfig)) {
						if(fkTable.get(fkAlias).toLowerCase().contains(fkCol.getColumnName().toLowerCase())) {
							Map<String, String> fkTab = new HashMap<String, String>();
							fkTab.put("fkTableName", fkCol.getTableName());
							fkTab.put("fkTableAlias", fkCol.getTableAlias());
							fkTableInfo.add(fkTab);
							genFkColumns.add(fkCol);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		genConfig.setFkTableInfo(fkTableInfo);
		genConfig.setTableAlias(tableInfo.getTableAlias());
		
		VelocityContext velocityContext = new VelocityContext();
		velocityContext.put("genConfig", genConfig);
		velocityContext.put("genColumns", genColumns);
		velocityContext.put("genFkColumns", genFkColumns);
		return velocityContext;
	}
	
	private String setTableName(String str, String style){
		String[] parts = str.split("_");
		String caseString = "";
		for (String part : parts){
			caseString += toProperCase(part);
		}
		switch(style) {
			case "camel":
				caseString = caseString.substring(0,1).toLowerCase() + caseString.substring(1);
				break;
			case "pascal":
				caseString = caseString.substring(0,1).toUpperCase() + caseString.substring(1);
				break;
		}
		return caseString;
	}
	
	private String toProperCase(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}
	
	public void createFile(String path, String name, String context, String string) {
		FileOutputStream outputStream; 
		try {
			Path currentRelativePath = Paths.get("");
			File f = new File(currentRelativePath.toAbsolutePath().toString() + "\\src\\main\\" + path + name);
			outputStream =  new FileOutputStream(currentRelativePath.toAbsolutePath().toString() + "\\src\\main\\" + path + name);
			byte[] strToBytes = context.getBytes();
			outputStream.write(strToBytes);
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
