package cam.slava.learn.service;

import cam.slava.learn.dto.TaskCreateDto;
import cam.slava.learn.dto.TaskDto;
import cam.slava.learn.dto.TaskPatchDto;
import cam.slava.learn.entity.TaskEntity;
import cam.slava.learn.entity.UserEntity;
import cam.slava.learn.repository.TaskRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository, ModelMapper modelMapper, UserService userService) {
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    public TaskDto getTaskDtoById(Long id) {
        TaskEntity taskEntity = taskRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        return modelMapper.map(taskEntity, TaskDto.class);
    }

    public List<TaskDto> getAllTaskDtoByUserID(Long currentUserId) {
        List<TaskEntity> allByUserEntityId = taskRepository.findAllByUserEntity_Id(currentUserId);

        return allByUserEntityId.stream()
                .map( taskEntity -> modelMapper.map(taskEntity, TaskDto.class))
                .toList();
    }

    public Optional<Long> createTask(TaskCreateDto taskCreateDto) {
        TaskEntity taskEntity = modelMapper.map(taskCreateDto, TaskEntity.class);
        Long userId = userService.getCurrentUserId()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated"));

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        taskEntity.setUserEntity(userEntity);

        try {
            taskRepository.save(taskEntity);
            return Optional.of(taskEntity.getId());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Long> deleteTaskById(Long id) {
        if (!taskRepository.existsById(id)) {
            return Optional.empty();
        }

        taskRepository.deleteById(id);
        return Optional.of(id);
    }

    public Optional<Long> patchTask(TaskPatchDto taskPatchDto) {
        Long userId = userService.getCurrentUserId()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not authenticated"
                ));

        TaskEntity taskEntity = taskRepository.findById(taskPatchDto.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found"
                ));

        if (!taskEntity.getUserEntity().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        Optional.ofNullable(taskPatchDto.getTaskTitle()).ifPresent(taskEntity::setTitle);
        Optional.ofNullable(taskPatchDto.getTaskDescription()).ifPresent(taskEntity::setDescription);
        Optional.ofNullable(taskPatchDto.getDone()).ifPresent(taskEntity::setDone);

        try {
            taskRepository.save(taskEntity);
            return Optional.of(taskEntity.getId());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
