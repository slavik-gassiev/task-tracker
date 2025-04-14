package cam.slava.learn.controller;

import cam.slava.learn.dto.AuthResponseDto;
import cam.slava.learn.dto.LoginDto;
import cam.slava.learn.dto.LogonDto;
import cam.slava.learn.provider.JwtTokenProvider;
import cam.slava.learn.service.UserService;
import cam.slava.learn.validation.UserValidation;
import cam.slava.learn.validation.ValidationError;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ValidationError validationError;
    private final UserValidation userValidation;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider, ValidationError validationError, UserValidation userValidation) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.validationError = validationError;
        this.userValidation = userValidation;
    }

    @PostMapping("/logon")
    public ResponseEntity<Object> logon( @Valid @RequestBody LogonDto logonDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validationError.mapValidationErrors(bindingResult);
        }

        if (userService.isUserExist(logonDto.getUserEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }

        Long userId = userService.createUser(logonDto.getUserEmail(), logonDto.getPassword())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to create user"
                ));

        String token = jwtTokenProvider.createToken(logonDto.getUserEmail(), logonDto.getPassword());
        AuthResponseDto authResponseDto = new AuthResponseDto(userId.toString(), token, "Logon successful");


        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(authResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login( @Valid @RequestBody LoginDto loginDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validationError.mapValidationErrors(bindingResult);
        }

        Long userId = userValidation.validateLogin(loginDto);

        String token = jwtTokenProvider.createToken(loginDto.getUserEmail(), loginDto.getPassword());
        AuthResponseDto authResponseDto = new AuthResponseDto(userId.toString(), token, "Login successful");

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(authResponseDto);
    }
}
