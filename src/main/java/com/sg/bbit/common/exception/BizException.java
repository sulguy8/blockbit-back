package com.sg.bbit.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class BizException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String errCode;
	
	public BizException(Exception e) {
		super(e);
	}
	public BizException(String errorMsg) {
		super(errorMsg);
	}

	public BizException(String errorMsg,String errCode) {
		super(errorMsg);
		this.errCode = errCode;
	}
	
	public BizException(Exception e, String string) {
		super(e);
	}
	
	public String getErrCode() {
		return errCode;
	}
	
	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}
}
