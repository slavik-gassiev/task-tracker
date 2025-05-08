package cam.slava.learn.controller;

import cam.slava.learn.dto.MessageDto;
import cam.slava.learn.dto.UserDto;
import cam.slava.learn.service.MessageService;
import cam.slava.learn.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class SchedulerController {
    private final UserService userService;
    private final MessageService messageService;

    public SchedulerController(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @Scheduled()
    public void start() {
        List<UserDto> allUsers = userService.getAllUsers();
        List<MessageDto> messages = messageService.createMessages(allUsers);

    }
}
