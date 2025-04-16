package cam.slava.learn.controller;

import cam.slava.learn.config.TestConfig;
import cam.slava.learn.dto.TaskCreateDto;
import cam.slava.learn.dto.TaskDto;
import cam.slava.learn.dto.TaskListResponseDto;
import cam.slava.learn.dto.TaskPatchDto;
import cam.slava.learn.entity.TaskEntity;
import cam.slava.learn.entity.UserEntity;
import cam.slava.learn.repository.TaskRepository;
import cam.slava.learn.service.TaskService;
import cam.slava.learn.service.UserService;
import cam.slava.learn.validation.TaskValidation;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestConfig.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TaskValidation taskValidation;

    @Test
    void shouldReturnTaskDtoAndOk() throws Exception{
        LocalDateTime now = LocalDateTime.now();
        Long taskId = 1L;
        Long userId = 5L;
        TaskDto taskDto = new TaskDto(taskId, "test_title", "test_desc", now, true);
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        taskEntity.setUserEntity(userEntity);

        Mockito.when(userService.getCurrentUserId()).thenReturn(Optional.of(5L));
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        Mockito.when(taskService.getTaskDtoById(taskId)).thenReturn(taskDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("test_title"))
                .andExpect(jsonPath("$.description").value("test_desc"))
                .andExpect(jsonPath("$.done").value(true));
    }

    @Test
    void shouldReturnUnauthorized_WhenUserNotAuthenticated() throws Exception{
        LocalDateTime now = LocalDateTime.now();
        Long taskId = 1L;
        Long userId = 5L;
        TaskDto taskDto = new TaskDto(taskId, "test_title", "test_desc", now, true);
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        taskEntity.setUserEntity(userEntity);

        Mockito.when(userService.getCurrentUserId()).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnNotFound_WhenTaskNotFound() throws Exception{
        LocalDateTime now = LocalDateTime.now();
        Long taskId = 1L;
        Long userId = 5L;
        TaskDto taskDto = new TaskDto(taskId, "test_title", "test_desc", now, true);
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        taskEntity.setUserEntity(userEntity);

        Mockito.when(userService.getCurrentUserId()).thenReturn(Optional.of(5L));
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnInternalServerError_UserIdAnTaskIdNotEqual() throws Exception{
        LocalDateTime now = LocalDateTime.now();
        Long taskId = 1L;
        Long userId = 5L;
        TaskDto taskDto = new TaskDto(taskId, "test_title", "test_desc", now, true);
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(11L);
        taskEntity.setUserEntity(userEntity);

        Mockito.when(userService.getCurrentUserId()).thenReturn(Optional.of(5L));
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnTaskListResponseDto_AndOk() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Long taskId = 1L;
        Long userId = 5L;

        TaskDto taskDto = new TaskDto(taskId, "test_title", "test_desc", now, true);
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        taskEntity.setUserEntity(userEntity);

        List<TaskEntity> taskEntities = List.of(taskEntity);
        List<TaskDto> tasksDto = List.of(taskDto);
        TaskListResponseDto taskListResponseDto = new TaskListResponseDto(tasksDto);

        Mockito.when(userService.getCurrentUserId()).thenReturn(Optional.of(userId));
        Mockito.when(taskRepository.findAllByUserEntity_Id(userId)).thenReturn(taskEntities);
        Mockito.when(modelMapper.map(taskEntity, TaskDto.class)).thenReturn(taskDto);
        Mockito.when(taskService.getAllTaskDtoByUserID(userId)).thenReturn(tasksDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.tasks[0].id").value(taskId))
                .andExpect(jsonPath("$.tasks[0].title").value("test_title"))
                .andExpect(jsonPath("$.tasks[0].description").value("test_desc"))
                .andExpect(jsonPath("$.tasks[0].done").value(true));
    }

    @Test
    void shouldReturnUserUnauthorized_WhenUserNotAuthenticated_GetAllTasks() throws Exception{
        Mockito.when(userService.getCurrentUserId()).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldCreateTask_AndReturnOk_AndCreatedTaskId() throws Exception {

        String json = """
                            {
                                "title": "test_title",
                                "description": "test_desc",
                                "done": true
                            }
                            """;

        Long taskId = 11L;

        Mockito.when(taskService.createTask(Mockito.any(TaskCreateDto.class))).thenReturn(Optional.of(11L));

        mockMvc.perform(MockMvcRequestBuilders.post("/tasks/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("11"))
                .andExpect(jsonPath("$.message").value("Task was successfully created"));
    }


    @Test
    void shouldPatchTask_AndReturnOk() throws Exception {

        String json = """
                            {
                                "title": "test_title",
                                "description": "test_desc",
                                "done": true
                            }
                            """;
        Long taskId = 11L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setUserEntity(userEntity);

        Mockito.when(taskService.patchTask(Mockito.any(TaskPatchDto.class))).thenReturn(Optional.of(11L));
        Mockito.when(userService.getCurrentUserId()).thenReturn(Optional.of(1L));
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        mockMvc.perform(MockMvcRequestBuilders.patch("/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(jsonPath("$.id").value("11"))
                .andExpect(jsonPath("$.message").value("Task patched successfully"));
    }
}