package cam.slava.learn.service;

import cam.slava.learn.entity.UserEntity;
import cam.slava.learn.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Long> createUser(String userEmail, String password) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(userEmail);
        userEntity.setPassword(passwordEncoder.encode(password));

        try {
           return  Optional.ofNullable(userRepository.save(userEntity).getId());
        } catch (Exception e) {
            return Optional.empty();
        }

    }

    public boolean isUserExist(String userEmail) {
        return userRepository.existsByUserName(userEmail);
    }

    public Optional<Long> findUser(String userEmail) {
        return userRepository.findByUserName(userEmail).map(UserEntity::getId);
    }

    public Optional<Long> getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return findUser(email);
    }

    public Optional<Long> checkPassword(String password, String userEmail) {
        Optional<UserEntity> userEntity = userRepository.findByUserName(userEmail);
        if (userEntity.isPresent() && passwordEncoder.matches(password, userEntity.get().getPassword())) {
            return Optional.of(userEntity.get().getId());
        }
        return Optional.empty();
    }
}
