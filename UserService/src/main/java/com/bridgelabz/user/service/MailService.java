package com.bridgelabz.user.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.bridgelabz.user.model.Email;

/**
 * Purpose : Class for sending mail
 * @author : Tasif Mohammed
 *
 */
@Component
public class MailService {

	@Autowired
	private JavaMailSender javaMailSender;

	/**
	 * Purpose : Function to send mail
	 * @param email
	 */
	@RabbitListener(queues = "${queueName}")
	public void sendMail(Email email) {

		SimpleMailMessage mail = new SimpleMailMessage();

		mail.setFrom(email.getFrom());
		mail.setTo(email.getTo());
		mail.setSubject(email.getSubject());
		mail.setText(email.getBody());

		System.out.println("Sending ....");
		javaMailSender.send(mail);
		System.out.println("Sent ....");
	}
}
