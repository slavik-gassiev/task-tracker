package cam.slava.learn.controller;

import cam.slava.learn.dto.TaskDto;
import cam.slava.learn.dto.TaskCreateDto;
import cam.slava.learn.dto.TaskListResponseDto;
import cam.slava.learn.dto.TaskPatchDto;
import cam.slava.learn.service.TaskService;
import cam.slava.learn.service.UserService;
import cam.slava.learn.validation.TaskValidation;
import cam.slava.learn.validation.ValidationError;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskValidation taskValidation;
    private final UserService userService;
    private final ValidationError validationError;

    public TaskController(TaskService taskService, TaskValidation taskValidation, UserService userService, ValidationError validationError) {
        this.taskService = taskService;
        this.taskValidation = taskValidation;
        this.userService = userService;
        this.validationError = validationError;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable("id")  Long id) {

        taskValidation.validateTaskAccess(id);
        TaskDto taskDto = taskService.getTaskDtoById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(taskDto);
    }

    @GetMapping()
    public ResponseEntity<TaskListResponseDto> getAllTasks() {

        Long currentUserId = taskValidation.validateUserAuthorization();

        List<TaskDto>  tasksDto = taskService.getAllTaskDtoByUserID(currentUserId);
        TaskListResponseDto taskListResponseDto = new TaskListResponseDto(tasksDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(taskListResponseDto);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createTask(@RequestBody @Valid TaskCreateDto taskCreateDto,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            validationError.mapValidationErrors(bindingResult);
        }

        Long createdTaskId = taskService.createTask(taskCreateDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Task not created"
                ));

        Map<String, String> response = new HashMap<>();
        response.put("id", String.valueOf(createdTaskId));
        response.put("message", "Task was successfully created");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchTaskById(@PathVariable("id") Long id,
                                                @Valid @RequestBody TaskPatchDto task,
                                                BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            validationError.mapValidationErrors(bindingResult);
        }

        taskValidation.validateTaskAccess(id);

        Long patchedTaskId = taskService.patchTask(task)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Task not patched"
                ));

        Map<String, String> response = new HashMap<>();
        response.put("id", String.valueOf(id));
        response.put("message", "Task patched successfully");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTaskById(@PathVariable("id") Long id) {
        taskValidation.validateTaskAccess(id);

        Long taskId = taskService.deleteTaskById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found"
                ));
        Map<String, String> response = new HashMap<>();
        response.put("id", String.valueOf(id));
        response.put("message", "Task deleted successfully");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
