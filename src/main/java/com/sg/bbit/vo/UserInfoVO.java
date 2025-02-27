package com.sg.bbit.vo;

import org.apache.ibatis.type.Alias;

import com.sg.bbit.common.vo.CommonVO;

import lombok.Data;

@Data
@Alias("usi")
public class UserInfoVO extends CommonVO {
	private Integer usiNum;
	protected String usiId;
	protected String usiPwd;
	private String usiName;
	private String usiPhoneNum;
	private String usiEmail;
	private String usiLastLogin;
	private Integer afiNum;
}