package cam.slava.learn.service;

import cam.slava.learn.entity.UserEntity;
import cam.slava.learn.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContextHolder securityContextHolder;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void shouldSaveUser_AndReturnId() {
        String userEmail = "test@test.com";
        String password = "test";
        String encodedPassword = "encodedPassword";

        UserEntity savedUser = new UserEntity();
        savedUser.setUserName(userEmail);
        savedUser.setPassword(password);
        savedUser.setId(1L);

        Mockito.when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        Mockito.when(userRepository.save(Mockito.any(UserEntity.class))).thenReturn(savedUser);

        Optional<Long> resultUser = userService.createUser(userEmail, password);

        assertTrue(resultUser.isPresent());
        assertEquals(1L, resultUser.get());

        Mockito.verify(passwordEncoder).encode(password);
        Mockito.verify(userRepository).save(Mockito.argThat(userEntity ->
                userEntity.getUserName().equals(userEmail) && userEntity.getPassword().equals(encodedPassword)));
    }

    @Test
    void shouldReturnTrue_IfUserExists() {
        String userEmail = "test@test.com";
        Mockito.when(userRepository.existsByUserName(userEmail)).thenReturn(true);

        boolean result = userService.isUserExist(userEmail);

        assertTrue(result);
        Mockito.verify(userRepository).existsByUserName(Mockito.argThat( name -> name.equals(userEmail)));
    }

    @Test
    void shouldReturnFalse_IfUserDoesNotExist() {
        String userEmail = "test@test.com";
        Mockito.when(userRepository.existsByUserName(userEmail)).thenReturn(false);
        boolean result = userService.isUserExist(userEmail);

        assertFalse(result);
    }

    @Test
    void shouldFindUserByName_AndReturnId() {
        String userEmail = "test@test.com";
        UserEntity savedUser = new UserEntity();
        savedUser.setUserName(userEmail);
        savedUser.setId(1L);

        Mockito.when(userRepository.findByUserName(userEmail)).thenReturn(Optional.of(savedUser));

        Optional<Long> result = userService.findUser(userEmail);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get());
    }

    @Test
    void shouldReturnEmpty_WhenUserNotFound() {
        String userEmail = "test@test.com";
        UserEntity savedUser = new UserEntity();
        savedUser.setUserName(userEmail);
        savedUser.setId(1L);

        Mockito.when(userRepository.findByUserName(userEmail)).thenReturn(Optional.empty());

        Optional<Long> result = userService.findUser(userEmail);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnUserId_IfUserAuthorised() {
        String userEmail = "test@test.com";
        Long userId = 47L;

        UserEntity savedUser = new UserEntity();
        savedUser.setUserName(userEmail);
        savedUser.setId(userId);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn(userEmail);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
        Mockito.when(userRepository.findByUserName(userEmail)).thenReturn(Optional.of(savedUser));

        Optional<Long> result = userService.getCurrentUserId();

        assertTrue(result.isPresent());
        assertEquals(userId, result.get());
    }

    @Test
    void shouldReturnEmpty_WhenUserNotAuthorised() {

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("");

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        Mockito.when(userRepository.findByUserName(Mockito.anyString())).thenReturn(Optional.empty());

        Optional<Long> result = userService.getCurrentUserId();

        assertFalse(result.isPresent());
    }
}