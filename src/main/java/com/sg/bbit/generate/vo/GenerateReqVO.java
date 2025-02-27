package com.sg.bbit.generate.vo;

import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GenerateReqVO {
	private String packageName;
	private String tableSchema;
	private Integer tableNum;
	private String tableName;
	private String tableAlias;
	private String tableCamelName;
	private String tablePascalName;
	private String generateTarget;
	private String controllerName;
	private String serviceName;
	private String mapperName;
	private String mapperXmlName;
	private String voName;
	private Set<Map<String, String>> fkTableInfo;
}
