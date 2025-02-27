package com.sg.bbit.generate.vo;

import com.sg.bbit.common.util.DataTypeConvertor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GenerateVO {
	private String tableSchema;
	private Integer tableNum;
	private String tableName;
	private String tableAlias;
	private String columnName;
	private String jdbcVarName;
	private Integer ordinalPosition;
	private String isNullable;
	private String isPrimaryKey;
	private String type;
	private String javaType;
	private String dataType;
	private String jdbcDataType;
	private Integer characterMaximumLength;
	private String columnKey;
	private String fkColumnMapp;
	
	public void setDataType(String dataType) {
		this.dataType = dataType;
		this.jdbcDataType = DataTypeConvertor.getJdbcDataType(dataType);
	}
	
	public void setColumnName(String columnName) {
		this.columnName = columnName;
		this.jdbcVarName = DataTypeConvertor.getJdbcVarName(columnName);
	}
}
