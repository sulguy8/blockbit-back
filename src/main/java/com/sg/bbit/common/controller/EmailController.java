package com.sg.bbit.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sg.bbit.common.service.EmailService;

@RestController
public class EmailController {
	@Autowired
	private EmailService emailService;
	
	@PostMapping("/certifiedEmail")
	public void certifiedEmail(@RequestParam("email") String email){
		emailService.mailSend(email);
	}
}
