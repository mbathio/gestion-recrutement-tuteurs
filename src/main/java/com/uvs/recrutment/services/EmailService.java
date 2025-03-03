package com.uvs.recrutment.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.MailException;
import jakarta.mail.MessagingException; // Utilisation de jakarta.mail
import jakarta.mail.internet.MimeMessage; // Utilisation de jakarta.mail

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendEmail(String to, String subject, String text) throws MailException, MessagingException {
        // Utilisation de MimeMessage de jakarta.mail
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setFrom("your-email@example.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        
        emailSender.send(message);
    }
}
