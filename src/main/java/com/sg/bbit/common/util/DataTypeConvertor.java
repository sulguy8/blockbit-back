package com.sg.bbit.common.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class DataTypeConvertor {
	private static List<String> stringTypes;
	private static List<String> dateTypes;
	
	@Value("${generate.convert.string-type}")
	public void setStringTypes(List<String> stringTypes) {
		DataTypeConvertor.stringTypes = stringTypes;
	}
	
	private static List<String> intTypes;
	@Value("${generate.convert.int-type}")
	public void setIntTypes(List<String> intTypes) {
		DataTypeConvertor.intTypes = intTypes;
	}
	
	private static List<String> doubleTypes;
	@Value("${generate.convert.double-type}")
	public void setDoubleTypes(List<String> doubleTypes) {
		DataTypeConvertor.doubleTypes = doubleTypes;
	}
	
	private static List<String> longTypes;
	@Value("${generate.convert.long-type}")
	public void setLongTypes(List<String> longTypes) {
		DataTypeConvertor.longTypes = longTypes;
	}
	
	@Value("${generate.convert.date-type}")
	public void setDateTypes(List<String> dateTypes) {
		DataTypeConvertor.dateTypes = dateTypes;
	}
	
	public static String getJdbcDataType(String dataType) {
		if(stringTypes.contains(dataType)) {
			return "String";
		}
		if(intTypes.contains(dataType)) {
			return "int";
		}
		if(doubleTypes.contains(dataType)) {
			return "double";
		}
		if(longTypes.contains(dataType)) {
			return "long";
		}
		if(dateTypes.contains(dataType)) {
			return "DateTime";
		}
		return null;
	}
	
	public static String getJdbcVarName(final String columnName) {
		final StringBuilder sb = new StringBuilder();
		final String[] columnNames = columnName.split("_");
		for(String col:columnNames) {
			sb.append(col.substring(0,1).toUpperCase());
			if(col.length()>1) {
				sb.append(col.substring(1).toLowerCase());
			}
		}
		String jdbcVarName = sb.toString();
		return jdbcVarName.substring(0,1).toLowerCase() + jdbcVarName.substring(1);
	}
}
