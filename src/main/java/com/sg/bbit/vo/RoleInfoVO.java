package com.sg.bbit.vo;

import org.apache.ibatis.type.Alias;

import com.sg.bbit.common.vo.CommonVO;

import lombok.Data;

@Data
@Alias("roi")
public class RoleInfoVO extends CommonVO {
	private Integer roiNum;
	protected String roiName;
	private String roiType;
}