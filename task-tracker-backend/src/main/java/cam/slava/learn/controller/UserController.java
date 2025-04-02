package cam.slava.learn.controller;

import cam.slava.learn.dto.LoginDto;
import cam.slava.learn.dto.LogonDto;
import cam.slava.learn.provider.JwtTokenProvider;
import cam.slava.learn.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Object> logon( @Valid @RequestBody LogonDto logonDto) {

        return ResponseEntity.ok("logon");
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login( @Valid @RequestBody LoginDto loginDto) {

        return ResponseEntity.ok("login");
    }
}
