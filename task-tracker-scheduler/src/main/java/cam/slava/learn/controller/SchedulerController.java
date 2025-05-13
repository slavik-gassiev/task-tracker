package cam.slava.learn.controller;

import cam.slava.learn.dto.MessageDto;
import cam.slava.learn.dto.UserDto;
import cam.slava.learn.service.KafkaService;
import cam.slava.learn.service.MessageService;
import cam.slava.learn.service.UserService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class SchedulerController {
    private final UserService userService;
    private final MessageService messageService;
    private final KafkaService kafkaService;

    public SchedulerController(UserService userService, MessageService messageService, KafkaService kafkaService) {
        this.userService = userService;
        this.messageService = messageService;
        this.kafkaService = kafkaService;
    }

    @Scheduled(cron = "0 0 18 * * *")
    public void start() {
        List<UserDto> allUsers = userService.getAllUsers();
        List<MessageDto> messages = messageService.createMessages(allUsers);
        kafkaService.sendMessages(messages);


    }
}
