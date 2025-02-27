package com.sg.bbit.common.vo;

import lombok.Data;

@Data
public class PaginationVO {
	private Integer pageNum;
	private Integer pageSize;
	private Boolean pageFlag;
	private Integer start;
	private Integer length;
}
