package cam.slava.learn.validation;

import cam.slava.learn.dto.LoginDto;
import cam.slava.learn.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserValidation {

    private final UserService userService;

    public UserValidation(UserService userService) {
        this.userService = userService;
    }

    public Long validateLogin(LoginDto loginDto) {

        if (!userService.isUserExist(loginDto.getUserEmail())) {
            throw  new ResponseStatusException(HttpStatus.CONFLICT, "User does not exist");
        }

        Long userId = userService.findUser(loginDto.getUserEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Failed to find user"
                ));

        userService.checkPassword(loginDto.getPassword(), loginDto.getUserEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.CONFLICT, "Invalid password"
                ));

        return userId;
    }
}
