package kafkagoal.paymentsprocessorservice.service;

public interface MailSender {
    boolean send(String emailAddress, String subject, String textMessage);
}
