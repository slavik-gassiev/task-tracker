package cam.slava.learn.service;

import cam.slava.learn.dto.MessageDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private final EmailSendService emailSendService;

    public KafkaConsumerService(EmailSendService emailSendService) {
        this.emailSendService = emailSendService;
    }

    @KafkaListener(
            topics = "EMAIL_SENDING_TASKS",
            containerFactory = "messageKafkaListenerContainerFactory"
    )
    public void messageListener(MessageDto messageDto) {
        emailSendService.sendEmail(messageDto.getUserEmail(), messageDto.getMessage());
    }
}
