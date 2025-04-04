package cam.slava.learn.service;

import cam.slava.learn.entity.TaskEntity;
import cam.slava.learn.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskService {
    private final UserService userService;
    private final TaskRepository taskRepository;

    public TaskService(UserService userService, TaskRepository taskRepository) {
        this.userService = userService;
        this.taskRepository = taskRepository;
    }

    public boolean validTaskId(Long id) {

        if (id.equals(0L)) {
            return false;
        }

        Long currentUserId = userService.getCurrentUserId().orElseThrow( () ->
                new RuntimeException( "User id not found" )
        );

        TaskEntity taskEntity = taskRepository.findById(id).orElseThrow( () ->
                new RuntimeException( "Task not found" )
        );


        return  currentUserId.equals(taskEntity.getUserEntity().getId());
    }
}
