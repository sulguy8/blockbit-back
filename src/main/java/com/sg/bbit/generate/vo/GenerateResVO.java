package com.sg.bbit.generate.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GenerateResVO {
	private String mapperXML;
	private String mapper;
	private String controller;
	private String service;
	private String vo;
	private String jsp;
	private String thymleaf;
}
