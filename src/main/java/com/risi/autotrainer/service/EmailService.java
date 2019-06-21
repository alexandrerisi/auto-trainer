package com.risi.autotrainer.service;

import com.risi.autotrainer.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    @Value("${email.user}")
    private String emailUser;
    @Value("${email.password}")
    private String emailPassword;
    @Autowired
    private UserService userService;

    void sendTemporaryPassword(User user) {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailUser, emailPassword);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailUser));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(user.getUsername())
            );
            message.setSubject("Auto Trainer Temporary Password");
            message.setText("Hello " + user.getUsername() + ",\n\n"
                    + "Your temporary password to Auto Trainer is -> " + userService.generateTemporaryPassword(user));

            Transport.send(message);
            logger.info("Temporary password created for -> " + user.getUsername());
        } catch (MessagingException e) {
            logger.severe("Failed to create temporary password for -> " + user.getUsername());
            e.printStackTrace();
        }
    }
}
