package com.sg.bbit.common.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HTMLController {
	@GetMapping("/html/**")
	public void page() {};
	
	@GetMapping("/")
	public String home() {
		return "index";
	}
}
