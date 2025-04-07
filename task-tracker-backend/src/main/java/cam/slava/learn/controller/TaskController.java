package cam.slava.learn.controller;

import cam.slava.learn.dto.TaskDto;
import cam.slava.learn.dto.TaskCreateDto;
import cam.slava.learn.dto.TaskPatchDto;
import cam.slava.learn.service.TaskService;
import cam.slava.learn.service.UserService;
import cam.slava.learn.validation.TaskValidation;
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

    public TaskController(TaskService taskService, TaskValidation taskValidation, UserService userService) {
        this.taskService = taskService;
        this.taskValidation = taskValidation;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable("id")  Long id) {

        taskValidation.validateTaskAccess(id);
        TaskDto taskDto = taskService.getTaskDtoById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(taskDto);
    }

    @GetMapping()
    public ResponseEntity<List<TaskDto>> getAllTasks() {

        Long currentUserId = userService.getCurrentUserId()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "User not logged in"
                ));

        List<TaskDto>  tasksDto = taskService.getAllTaskDtoByUserID(currentUserId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(tasksDto);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createTask(@RequestBody @Valid TaskCreateDto taskCreateDto,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError ->
                errors.put(fieldError.getField(), fieldError.getDefaultMessage())
            );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        Long createdTaskId = taskService.createTask(taskCreateDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Task not created"
                ));

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTaskId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchTaskById(@PathVariable("id") Long id,
                                                @Valid @RequestBody TaskPatchDto task,
                                                BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(
                    fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage())
            );

            return ResponseEntity.badRequest().body(errors);
        }

        taskValidation.validateTaskAccess(id);

        Long patchedTaskId = taskService.patchTask(task)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Task not patched"
                ));

        return ResponseEntity.status(HttpStatus.OK).body(patchedTaskId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTaskById(@PathVariable("id") Long id) {
        taskValidation.validateTaskAccess(id);

        Long taskId = taskService.deleteTaskById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found"
                ));

        return ResponseEntity.status(HttpStatus.OK).body(taskId);
    }
}
