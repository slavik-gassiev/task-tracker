package cam.slava.learn.config;

import cam.slava.learn.filter.JwtTokenFilter;
import cam.slava.learn.provider.JwtTokenProvider;
import cam.slava.learn.repository.TaskRepository;
import cam.slava.learn.repository.UserRepository;
import cam.slava.learn.service.TaskService;
import cam.slava.learn.service.UserService;
import cam.slava.learn.validation.TaskValidation;
import cam.slava.learn.validation.ValidationError;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class TestConfig {

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return Mockito.mock(JwtTokenProvider.class);
    }

    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return Mockito.mock(JwtTokenFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Mockito.mock(PasswordEncoder.class);
    }

    @Bean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }

    @Bean
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }

    @Bean
    public TaskService taskService() {
        return Mockito.mock(TaskService.class);
    }

    @Bean
    public TaskRepository taskRepository() {
        return Mockito.mock(TaskRepository.class);
    }

    @Bean
    public ValidationError validationError() {
        return Mockito.mock(ValidationError.class);
    }

    @Bean
    public TaskValidation taskValidation() {
        return Mockito.mock(TaskValidation.class);
    }

}
