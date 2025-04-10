package cam.slava.learn.controller;

import cam.slava.learn.dto.AuthResponseDto;
import cam.slava.learn.dto.LoginDto;
import cam.slava.learn.dto.LogonDto;
import cam.slava.learn.provider.JwtTokenProvider;
import cam.slava.learn.service.UserService;
import cam.slava.learn.validation.ValidationError;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(UserControllerTest.TestConfig.class)
class UserControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Внедрим моки через тестовую конфигурацию
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ValidationError validationError;

    public UserControllerTest(WebApplicationContext context,
                              UserService userService,
                              JwtTokenProvider jwtTokenProvider,
                              ValidationError validationError) {
        // Настроим MockMvc через контекст приложения, который подгрузит наш контроллер UserController
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.validationError = validationError;
    }

    @AfterEach
    void tearDown() {
        // При необходимости можно сбросить состояние между тестами
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }

        @Bean
        public JwtTokenProvider jwtTokenProvider() {
            return Mockito.mock(JwtTokenProvider.class);
        }

        @Bean
        public ValidationError validationError() {
            // Для простоты можно сделать, чтобы метод mapValidationErrors возвращал сразу фиктивный ResponseEntity
            ValidationError ve = Mockito.mock(ValidationError.class);
            Mockito.when(ve.mapValidationErrors(Mockito.any())).thenReturn(
                    ResponseEntity.badRequest().body("Validation errors")
            );
            return ve;
        }
    }

    @Test
    void logonShouldReturnConflictWhenUserAlreadyExists() throws Exception {
        // Подготовка DTO для логона
        LogonDto dto = new LogonDto("test@test.com", "password123");
        // Эмулируем, что пользователь уже существует
        Mockito.when(userService.isUserExist(eq(dto.getUserEmail()))).thenReturn(true);

        mockMvc.perform(post("/auth/logon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(status().reason(containsString("User already exists")));
    }

    @Test
    void loginShouldReturnSuccessIfUserExists() throws Exception {
        // Подготовка DTO для логина
        LoginDto dto = new LoginDto("test@test.com", "password123");
        // Эмулируем, что пользователь существует
        Mockito.when(userService.isUserExist(eq(dto.getUserEmail()))).thenReturn(true);
        Mockito.when(userService.findUser(eq(dto.getUserEmail()))).thenReturn(Optional.of(1L));
        // Эмулируем создание токена
        String token = "fake-jwt-token";
        Mockito.when(jwtTokenProvider.createToken(eq(dto.getUserEmail()), eq(dto.getPassword()))).thenReturn(token);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(content().string(containsString("Login successful")));
    }
}
