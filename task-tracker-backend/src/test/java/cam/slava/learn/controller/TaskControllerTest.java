package cam.slava.learn.controller;

import cam.slava.learn.dto.TaskDto;
import cam.slava.learn.entity.TaskEntity;
import cam.slava.learn.entity.UserEntity;
import cam.slava.learn.repository.TaskRepository;
import cam.slava.learn.service.TaskService;
import cam.slava.learn.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskControllerTest.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestConfiguration.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

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
    void getAllTasks() {
    }

    @Test
    void createTask() {
    }

    @Test
    void patchTaskById() {
    }

    @Test
    void deleteTaskById() {
    }
}