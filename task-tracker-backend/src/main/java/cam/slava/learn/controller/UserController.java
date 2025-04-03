package cam.slava.learn.controller;

import cam.slava.learn.dto.AuthResponseDto;
import cam.slava.learn.dto.LoginDto;
import cam.slava.learn.dto.LogonDto;
import cam.slava.learn.provider.JwtTokenProvider;
import cam.slava.learn.service.UserService;
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

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/logon")
    public ResponseEntity<Object> logon( @Valid @RequestBody LogonDto logonDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach( fieldError ->
                    errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        if (userService.isUserExist(logonDto.getUserEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }

        Long userId = userService.createUser(logonDto.getUserEmail(), logonDto.getPassword())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
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
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach( fieldError ->
                    errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        if (!userService.isUserExist(loginDto.getUserEmail())) {
            throw  new ResponseStatusException(HttpStatus.CONFLICT, "User does not exist");
        }

        Long userId = userService.findUser(loginDto.getUserEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Failed to find user"
                ));

        String token = jwtTokenProvider.createToken(loginDto.getUserEmail(), loginDto.getPassword());
        AuthResponseDto authResponseDto = new AuthResponseDto(userId.toString(), token, "Login successful");

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(authResponseDto);
    }
}
