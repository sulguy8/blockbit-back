package com.sg.bbit.security.vo;

import org.springframework.security.core.GrantedAuthority;

import com.sg.bbit.vo.RoleInfoVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthUserRole extends RoleInfoVO implements GrantedAuthority {
	private static final long serialVersionUID = 1L;
	
	@Override
	public String getAuthority() {
		return roiName;
	}
}