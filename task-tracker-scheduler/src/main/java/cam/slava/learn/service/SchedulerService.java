package cam.slava.learn.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService {
    private final UserService userService;

    public SchedulerService(UserService userService) {
        this.userService = userService;
    }

    @Scheduled()
    public void start() {

    }
}
