package com.example.smart_tiffin.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            // Use MimeMessage for HTML emails
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("hm849359@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // 'true' enables HTML

            mailSender.send(message);
            System.out.println("HTML Email sent successfully to " + to);

        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());

            // FALLBACK FOR DEMO: Print the raw link so you can test it
            System.out.println("--- DEMO MODE: EMAIL SIMULATION ---");
            System.out.println("To: " + to);
            // We strip HTML tags for console readability, but show the URL
            System.out.println("Content: " + body);
            System.out.println("-----------------------------------");
        }
    }
}