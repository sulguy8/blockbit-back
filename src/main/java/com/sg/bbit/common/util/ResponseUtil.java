package com.sg.bbit.common.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletResponse;

public class ResponseUtil{
	public static void jsonWrite(HttpServletResponse res, Map<String,Object> body) throws IOException {
		res.setContentType("application/json;charset=UTF-8");
		PrintWriter out = res.getWriter();
		JSONObject json = new JSONObject(body);
		out.print(json);
		out.flush();
		out.close();
	}
	
	public static void errorJsonWrite(HttpServletResponse res, Map<String,Object> body, HttpStatus status) throws IOException {
		res.setStatus(status.value());
		res.setContentType("application/json;charset=UTF-8");
		PrintWriter out = res.getWriter();
		JSONObject json = new JSONObject(body);
		out.print(json);
		out.flush();
		out.close();
	}
	
	public static void jsonWrite(HttpServletResponse res, String json) throws IOException {
		res.setContentType("application/json;charset=UTF-8");
		PrintWriter out = res.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}
}
