package com.sg.bbit.common.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class ErrorController extends BasicErrorController {
	private Logger log = LoggerFactory.getLogger(getClass());

	public ErrorController(ErrorAttributes errorAttributes, ServerProperties serverProperties, List<ErrorViewResolver> errorViewResolvers) {
		super(errorAttributes, serverProperties.getError(), errorViewResolvers);
	}

	@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
		log(request);
		return customErrorHtml(request, response);
	}

	public ModelAndView customErrorHtml(HttpServletRequest request, HttpServletResponse response) {
		HttpStatus status = this.getStatus(request);
		final int statusCode = status.value();
		Map<String, Object> model = new java.util.HashMap<>(Collections.unmodifiableMap(this.getErrorAttributes(request, this.getErrorAttributeOptions(request, MediaType.TEXT_HTML))));
		response.setStatus(statusCode);
		ModelAndView modelAndView = this.resolveErrorView(request, response, status, model);
		return modelAndView != null ? modelAndView : new ModelAndView("/error", model);
	}

	private void log(HttpServletRequest request) {
		log.error("==ErrorController ip:{}, ErrorAttributes:{}", request.getRemoteAddr(), getErrorAttributes(request));
	}

	private Map<String, Object> getErrorAttributes(HttpServletRequest request) {
		boolean includeStackTrace = false;
		includeStackTrace = isIncludeStackTrace(request, MediaType.TEXT_HTML);

		return super.getErrorAttributes(request, includeStackTrace ? ErrorAttributeOptions.of(new ErrorAttributeOptions.Include[]{ErrorAttributeOptions.Include.STACK_TRACE}) : ErrorAttributeOptions.defaults());
	}
}