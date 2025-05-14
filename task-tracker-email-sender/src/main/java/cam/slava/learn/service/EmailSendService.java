package cam.slava.learn.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSendService {
    private final JavaMailSender mailSender;
    private String subject = "Task Tracker";

    @Value("${spring.mail.username}")
    private String from;

    public EmailSendService(JavaMailSender  mailSender){
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
