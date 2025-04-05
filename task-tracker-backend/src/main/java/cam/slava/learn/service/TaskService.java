package cam.slava.learn.service;

import cam.slava.learn.dto.TaskDto;
import cam.slava.learn.entity.TaskEntity;
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

    public TaskService(TaskRepository taskRepository, ModelMapper modelMapper) {
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
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
}
