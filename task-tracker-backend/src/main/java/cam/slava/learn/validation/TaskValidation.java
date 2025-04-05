package cam.slava.learn.validation;

import cam.slava.learn.entity.TaskEntity;
import cam.slava.learn.repository.TaskRepository;
import cam.slava.learn.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class TaskValidation {
    private final UserService userService;
    private final TaskRepository taskRepository;

    public TaskValidation(UserService userService, TaskRepository taskRepository) {
        this.userService = userService;
        this.taskRepository = taskRepository;
    }

    public void validateTaskAccess(Long taskId) {

        if (taskId == null || taskId <= 0L) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task id");
        }

        Long currentUserId = userService.getCurrentUserId()
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated"));

        TaskEntity taskEntity = taskRepository.findById(taskId)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        if (!taskEntity.getUserEntity().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Task access denied");
        }
    }
}
