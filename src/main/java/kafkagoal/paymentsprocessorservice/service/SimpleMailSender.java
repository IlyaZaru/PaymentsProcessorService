package kafkagoal.paymentsprocessorservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleMailSender implements MailSender {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String username;

    @Override
    public boolean send(String clientEmailAddress, String subject, String textMessage) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(username);
        mailMessage.setTo(clientEmailAddress);
        mailMessage.setSubject(subject);
        mailMessage.setText(textMessage);

        try {
            javaMailSender.send(mailMessage);
            return true;
        } catch (MailException e) {
            log.error("Error of sending payment notification to {}, error: {}", clientEmailAddress, e);
            return false;
        }
    }
}
