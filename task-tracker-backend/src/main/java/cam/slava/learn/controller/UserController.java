package cam.slava.learn.controller;

import cam.slava.learn.dto.LoginDto;
import cam.slava.learn.dto.LogonDto;
import cam.slava.learn.provider.JwtTokenProvider;
import cam.slava.learn.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

        return ResponseEntity.ok("logon");
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login( @Valid @RequestBody LoginDto loginDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach( fieldError ->
                    errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        return ResponseEntity.ok("login");
    }
}
