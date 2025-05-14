package cam.slava.learn.controller;

import cam.slava.learn.dto.MessageDto;
import cam.slava.learn.dto.UserDto;
import cam.slava.learn.service.KafkaProducerService;
import cam.slava.learn.service.MessageService;
import cam.slava.learn.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
public class SchedulerController {
    private final UserService userService;
    private final MessageService messageService;
    private final KafkaProducerService kafkaProducerService;

    public SchedulerController(UserService userService, MessageService messageService, KafkaProducerService kafkaProducerService) {
        this.userService = userService;
        this.messageService = messageService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Scheduled(cron = "0 0 18 * * *")
    public void start() {
        List<UserDto> allUsers = userService.getAllUsers();
        List<MessageDto> messages = messageService.createMessages(allUsers);
        kafkaProducerService.sendMessages(messages);


    }
}
