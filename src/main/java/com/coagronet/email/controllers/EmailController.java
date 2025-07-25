package com.coagronet.email.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.email.services.EmailService;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {

	@Autowired
	private EmailService emailService;

	@GetMapping("/send")
	public String sendEmail() {
		emailService.sendSimpleMessage("juanjosecastrogutierrez@gmail.com", "Prueba backend", "LOrem ipsum dolor.");
		return "Email sent!";
	}

}
