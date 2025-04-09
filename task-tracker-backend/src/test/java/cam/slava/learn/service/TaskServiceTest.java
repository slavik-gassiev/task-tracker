package cam.slava.learn.service;

import cam.slava.learn.dto.TaskCreateDto;
import cam.slava.learn.dto.TaskDto;
import cam.slava.learn.dto.TaskPatchDto;
import cam.slava.learn.entity.TaskEntity;
import cam.slava.learn.entity.UserEntity;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    void shouldGetThrowExceptionWhenTaskDoesNotExist() {
        Long taskId = 1L;
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                () -> taskService.getTaskDtoById(taskId));

        assertEquals(HttpStatus.NOT_FOUND, responseStatusException.getStatusCode());
        assertEquals("Task not found", responseStatusException.getReason());
    }

    @Test
    void shouldGetTaskDtoByTaskIdWhenTaskExists() {
        Long taskId = 1L;
        TaskDto taskDto = new TaskDto();
        taskDto.setId(taskId);
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        Mockito.when(modelMapper.map(taskEntity, TaskDto.class)).thenReturn(taskDto);

        TaskDto actualDto = taskService.getTaskDtoById(taskId);

        assertNotNull(actualDto);
        assertEquals(taskDto.getId(), actualDto.getId());
    }

    @Test
    void shouldGetAllTaskDto_WhenTaskExists() {
        Long userId = 1L;
        LocalDateTime localDateTime =LocalDateTime.now();

        TaskEntity taskEntity = new TaskEntity(1L, "Test 1", "desk", false, localDateTime, null);
        TaskEntity taskEntity2 = new TaskEntity(2L, "Test 2", "desk", false, localDateTime, null);

        List<TaskEntity> taskEntities = List.of(taskEntity, taskEntity2);

        TaskDto taskDto = new TaskDto(1L, "Test 1", "desk", localDateTime, false);
        TaskDto taskDto2 = new TaskDto(2L, "Test 2", "desk", localDateTime, false);

        Mockito.when(taskRepository.findAllByUserEntity_Id(userId)).thenReturn(taskEntities);
        Mockito.when(modelMapper.map(taskEntity, TaskDto.class)).thenReturn(taskDto);
        Mockito.when(modelMapper.map(taskEntity2, TaskDto.class)).thenReturn(taskDto2);

        List<TaskDto> result = taskService.getAllTaskDtoByUserID(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(taskDto.getId(), result.get(0).getId());
        assertEquals(taskDto2.getTitle(), result.get(1).getTitle());

        Mockito.verify(modelMapper).map(taskEntity, TaskDto.class);
        Mockito.verify(modelMapper).map(taskEntity2, TaskDto.class);
    }

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
    void shouldNotCreateTaskWhenUserIsNotLoggedIn() {
        TaskCreateDto taskDto = new TaskCreateDto("test", "desk", false);
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(1L);

        Mockito.when(userService.getCurrentUserId()).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> taskService.createTask(taskDto));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("User not authenticated", exception.getReason());
    }

    @Test
    void shouldDeleteTaskAndReturnId() {
        Long id = 1L;
        Mockito.when(taskRepository.existsById(id)).thenReturn(true);

        Optional<Long> optionalId = taskService.deleteTaskById(id);

        assertTrue(optionalId.isPresent());
        assertEquals(id, optionalId.get());
    }

    @Test
    void shouldNotDeleteTaskAndReturnOptionalEmpty() {
        Long id = 1L;
        Mockito.when(taskRepository.existsById(id)).thenReturn(false);
        Optional<Long> optionalId = taskService.deleteTaskById(id);
        assertTrue(optionalId.isEmpty());

        Mockito.verify(taskRepository, Mockito.never()).deleteById(id);
    }

    @Test
    void patchTaskAnd_ReturnId() {
        final Long userId = 1L;
        final Long taskId = 2L;

        TaskEntity taskEntity = new TaskEntity();
        UserEntity userEntity = new UserEntity();

        userEntity.setId(userId);
        taskEntity.setId(taskId);
        taskEntity.setUserEntity(userEntity);

        final TaskPatchDto taskPatchDto = new TaskPatchDto(taskId, "test", "desk", false);

        Mockito.when(userService.getCurrentUserId()).thenReturn(Optional.of(1L));
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        Mockito.when(taskRepository.save(taskEntity)).thenReturn(taskEntity);

        Optional<Long> resultId = taskService.patchTask(taskPatchDto);

        assertTrue(resultId.isPresent());
        assertEquals(taskEntity.getId(), resultId.get());
        assertEquals(taskPatchDto.getTaskTitle(), taskEntity.getTitle());
        assertEquals(taskPatchDto.getTaskDescription(), taskEntity.getDescription());
        assertEquals(taskPatchDto.getDone(), taskEntity.getDone());
    }

    @Test
    void shouldThrowException_WhenUserIsNotLoggedIn() {
        final Long userId = 1L;
        final Long taskId = 2L;

        TaskEntity taskEntity = new TaskEntity();
        UserEntity userEntity = new UserEntity();

        userEntity.setId(userId);
        taskEntity.setId(taskId);
        taskEntity.setUserEntity(userEntity);

        final TaskPatchDto taskPatchDto = new TaskPatchDto(taskId, "test", "desk", false);

        Mockito.when(userService.getCurrentUserId()).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> taskService.patchTask(taskPatchDto)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("User not authenticated", exception.getReason());
    }

    @Test
    void shouldThrowException_WhenTaskIsNotExist() {
        final Long userId = 1L;
        final Long taskId = 2L;

        TaskEntity taskEntity = new TaskEntity();
        UserEntity userEntity = new UserEntity();

        userEntity.setId(userId);
        taskEntity.setId(taskId);
        taskEntity.setUserEntity(userEntity);

        final TaskPatchDto taskPatchDto = new TaskPatchDto(taskId, "test", "desk", false);

        Mockito.when(userService.getCurrentUserId()).thenReturn(Optional.of(1L));
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> taskService.patchTask(taskPatchDto)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Task not found", exception.getReason());
    }
}