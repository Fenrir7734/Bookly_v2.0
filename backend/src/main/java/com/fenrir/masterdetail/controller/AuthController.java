package com.fenrir.masterdetail.controller;

import com.fenrir.masterdetail.dto.JwtTokenDTO;
import com.fenrir.masterdetail.dto.SignInDTO;
import com.fenrir.masterdetail.dto.SignUpDTO;
import com.fenrir.masterdetail.dto.UserResponseDTO;
import com.fenrir.masterdetail.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@AllArgsConstructor
@RestController
@RequestMapping(
        path = "/api/auth",
        produces = "application/hal+json"
)
public class AuthController {
    private UserService userService;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpDTO signUpDTO, UriComponentsBuilder builder) {
        UserResponseDTO user = userService.registerUser(signUpDTO);
        String username = user.getUsername();
        URI location = builder.replacePath("/api/users/{username}")
                .buildAndExpand(username)
                .toUri();
        return ResponseEntity.created(location).body(user);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signIn(@RequestBody SignInDTO signInDTO) {
        JwtTokenDTO token = userService.authenticateUser(signInDTO);
        return ResponseEntity.ok(token);
    }

    @PostMapping(value = "/valid", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateToken(@RequestBody JwtTokenDTO jwtTokenDTO) {
        return userService.validateToken(jwtTokenDTO)
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
