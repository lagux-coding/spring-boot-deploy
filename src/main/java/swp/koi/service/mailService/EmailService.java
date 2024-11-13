package swp.koi.service.mailService;

import org.springframework.scheduling.annotation.Async;

public interface EmailService {

    void sendEmail(String to, String subject, String body);

}
