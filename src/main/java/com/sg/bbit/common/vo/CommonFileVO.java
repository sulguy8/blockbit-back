package com.sg.bbit.common.vo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommonFileVO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String path;
	private String name;
	private String context;
	private String string;
	private boolean reload;
}
