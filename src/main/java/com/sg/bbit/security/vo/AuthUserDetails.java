package com.sg.bbit.security.vo;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.sg.bbit.vo.UserInfoVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthUserDetails extends UserInfoVO implements UserDetails{
	private static final long serialVersionUID = 1L;
	
	private String token;
	private String refreshToken;
	private List<AuthUserRole> roles;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new HashSet<>();
		this.roles.forEach(role -> {
			authorities.add(() -> "ROLE_" + role.getAuthority());
		});
		return authorities;
	}

	@Override
	public String getPassword() {
		return usiPwd;
	}

	@Override
	public String getUsername() {
		return usiId;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return "1".equals(active);
	}
}
