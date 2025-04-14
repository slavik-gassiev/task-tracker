package cam.slava.learn.controller;

import cam.slava.learn.config.TestConfig;
import cam.slava.learn.dto.LoginDto;
import cam.slava.learn.provider.JwtTokenProvider;
import cam.slava.learn.service.UserService;
import cam.slava.learn.validation.UserValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(jwtTokenProvider);
    }

    @Test
    void shouldReturnOk_WhenUserIsLoggedIn() throws Exception {
        String json = """
                {
                "userEmail": "test@test.com",
                "password": "test123"
                }
                """;

        Mockito.when(userService.isUserExist("test@test.com")).thenReturn(false);
        Mockito.when(userService.createUser("test@test.com", "test123")).thenReturn(Optional.of(1L));
        Mockito.when(jwtTokenProvider.createToken("test@test.com", "test123")).thenReturn("mapped-token");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/logon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, "Bearer mapped-token"));
    }

    @Test
    void shouldReturnConflict_WhenUserAlreadyExists() throws Exception {
        String json = """
                {
                "userEmail": "test@test.com",
                "password": "test123"
                }
                """;

        Mockito.when(userService.isUserExist("test@test.com")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/logon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isConflict());

        Mockito.verify(jwtTokenProvider, Mockito.never()).createToken("test@test.com", "test123");
    }

    @Test
    void shouldReturnOk_WhenUserLoginSuccess() throws Exception {
        String json = """
                {
                "userEmail": "test@test.com",
                "password": "test123"
                }
                """;

        Mockito.when(userService.isUserExist("test@test.com")).thenReturn(true);
        Mockito.when(userService.findUser("test@test.com")).thenReturn(Optional.of(1L));
        Mockito.when(jwtTokenProvider.createToken("test@test.com", "test123")).thenReturn("mapped-token");
        Mockito.when(userService.checkPassword("test123", "test@test.com")).thenReturn(Optional.of(1L));

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, "Bearer mapped-token"));
    }

    @Test
    void shouldReturnConflict_WhenUserNotFound() throws Exception {
        String json = """
                {
                "userEmail": "test@test.com",
                "password": "test123"
                }
                """;

        Mockito.when(userService.isUserExist("test@test.com")).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isConflict());

        Mockito.verify(jwtTokenProvider, Mockito.never()).createToken("test@test.com", "test123");
    }

    @Test
    void shouldReturnConflict_WhenPasswordIsIncorrect() throws Exception {
        String json = """
                {
                "userEmail": "test@test.com",
                "password": "test123"
                }
                """;


        Mockito.when(userService.isUserExist("test@test.com")).thenReturn(true);
        Mockito.when(userService.findUser("test@test.com")).thenReturn(Optional.of(1L));
        Mockito.when(userService.checkPassword("test123", "test@test.com")).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isConflict());
    }
}