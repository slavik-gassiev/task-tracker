package cam.slava.learn.service;

import cam.slava.learn.dto.MessageDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, MessageDto> kafkaTemplate;

    @Value(value = "${spring.kafka.topic}")
    private String topic;

    public KafkaProducerService(KafkaTemplate<String, MessageDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessages(List<MessageDto> messages) {
        for (MessageDto messageDto :    messages) {
            kafkaTemplate.send(topic, messageDto);
        }
    }
}
