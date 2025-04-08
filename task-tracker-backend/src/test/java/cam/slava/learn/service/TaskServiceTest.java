package cam.slava.learn.service;

import cam.slava.learn.dto.TaskCreateDto;
import cam.slava.learn.dto.TaskDto;
import cam.slava.learn.entity.TaskEntity;
import cam.slava.learn.repository.TaskRepository;
import cam.slava.learn.validation.TaskValidation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldCreateTaskAndReturnId() {
        TaskCreateDto taskDto = new TaskCreateDto("test", "desk", false);
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(1L);

        Mockito.when(userService.getCurrentUserId()).thenReturn(Optional.of(42L));
        Mockito.when(modelMapper.map(taskDto, TaskEntity.class)).thenReturn(taskEntity);
        Mockito.when(taskRepository.save(taskEntity)).thenReturn(taskEntity);

        Optional<Long> task = taskService.createTask(taskDto);

        assertTrue(task.isPresent());
        assertEquals(taskEntity.getId(), task.get());

    }

    @Test
    void shouldDeleteTaskAndReturnId() {
        TaskCreateDto taskDto = new TaskCreateDto("test", "desk", false);
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(1L);

        Mockito.when(userService.getCurrentUserId()).thenReturn(Optional.of(42L));
        Mockito.when(modelMapper.map(taskDto, TaskEntity.class)).thenReturn(taskEntity);
        Mockito.when(taskRepository.save(taskEntity)).thenReturn(taskEntity);

        taskService.createTask(taskDto);

        Mockito.when(taskRepository.existsById(taskEntity.getId())).thenReturn(true);

        Optional<Long> id = taskService.deleteTaskById(taskEntity.getId());

        assertTrue(id.isPresent());
        assertEquals(taskEntity.getId(), id.get());
    }
}