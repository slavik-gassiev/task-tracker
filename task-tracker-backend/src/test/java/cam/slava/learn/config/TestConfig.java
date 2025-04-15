package cam.slava.learn.config;

import cam.slava.learn.filter.JwtTokenFilter;
import cam.slava.learn.provider.JwtTokenProvider;
import cam.slava.learn.repository.TaskRepository;
import cam.slava.learn.repository.UserRepository;
import cam.slava.learn.service.TaskService;
import cam.slava.learn.service.UserService;
import cam.slava.learn.validation.TaskValidation;
import cam.slava.learn.validation.UserValidation;
import cam.slava.learn.validation.ValidationError;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration(proxyBeanMethods = false)
public class TestConfig {

    @Bean
    public ModelMapper modelMapper() {
        return Mockito.mock(ModelMapper.class);
    }

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
    public TaskValidation taskValidation(UserService userService, TaskRepository taskRepository) {
        return new TaskValidation(userService, taskRepository);
    }

    @Bean
    public UserValidation userValidation(UserService userService) {
        return new UserValidation(userService);
    }
}
