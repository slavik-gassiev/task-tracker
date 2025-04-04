package cam.slava.learn.controller;

import cam.slava.learn.dto.TaskDto;
import cam.slava.learn.dto.TaskCreateDto;
import cam.slava.learn.dto.TaskPatchDto;
import cam.slava.learn.service.TaskService;
import cam.slava.learn.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        return null;
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createTask(@RequestBody @Valid TaskCreateDto taskCreateDto, BindingResult bindingResult) {

        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable("id")  Long id) {
        if (taskService.validTaskId(id)) {

        }
        return null;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskCreateDto> patchTaskById(@PathVariable("id") Long id,
                                                        @RequestBody @Valid TaskPatchDto task,
                                                        BindingResult bindingResult) {

        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTaskById(@PathVariable("id") @Valid Long id) {
        return null;
    }
}
