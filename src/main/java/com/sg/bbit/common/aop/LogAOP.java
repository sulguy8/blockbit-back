package com.sg.bbit.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class LogAOP {
	@Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
	public void restController() {};
	
	@Around("restController()")
	public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
		String signatureInfo = getSignatureInfo(joinPoint);
		log.debug("before =>{} ", signatureInfo);
		Object retVal = joinPoint.proceed();
		log.debug("after =>{} " , signatureInfo + (retVal != null ? " : " + retVal : ""));
		return retVal;
	}

	private String getSignatureInfo(JoinPoint joinPoint) {
		String signatureName = joinPoint.getSignature().getName();
		String className = joinPoint.getTarget().getClass().getSimpleName();
		StringBuilder sb = new StringBuilder();
		sb.append(className).append('.').append(signatureName).append('(');
		Object[] args = joinPoint.getArgs();
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i] instanceof String)
					sb.append('\"');
				sb.append(args[i]);
				if (args[i] instanceof String)
					sb.append('\"');
				if (i < args.length - 1) {
					sb.append(',');
				}
			}
		}
		sb.append(')');
		return sb.toString();
	}
}
