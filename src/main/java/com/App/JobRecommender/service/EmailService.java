package com.App.JobRecommender.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private JavaMailSender mailSender;

    @Value("{spring.mail.username}")
    private String sentby;

    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    public void sendMail(String toMail, String subject, String body){
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(sentby);
        message.setTo(toMail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
