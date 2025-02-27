package com.sg.bbit.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	private final JavaMailSender javaMailSender;

	@Autowired
	public EmailService(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public void mailSend(String email) {
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setFrom("k.y.kim@runtime.co.kr");
		simpleMailMessage.setTo(email);
		simpleMailMessage.setSubject("title");
		simpleMailMessage.setText("Text Mail Sender");

		javaMailSender.send(simpleMailMessage);
	}
}
