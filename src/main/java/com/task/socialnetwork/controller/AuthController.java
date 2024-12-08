package com.task.socialnetwork.controller;

import com.task.socialnetwork.dto.AuthRequestDTO;
import com.task.socialnetwork.dto.AuthResponseDTO;
import com.task.socialnetwork.dto.RegistrationRequestDTO;
import com.task.socialnetwork.service.AuthService;
import com.task.socialnetwork.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final AuthService authService;

  @Operation(
      summary = "Login user",
      description = "Authenticate a user with username and password, returning a JWT token upon success."
  )
  @PostMapping("/login")
  public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO authRequestDTO) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(),
            authRequestDTO.getPassword())
    );
    UserDetails userDetails = userDetailsService.loadUserByUsername(authRequestDTO.getUsername());
    String token = jwtService.generateToken(userDetails);
    return ResponseEntity.ok(new AuthResponseDTO(token));
  }

  @Operation(
      summary = "Register a new user",
      description = "Register a new user by providing their details in the request body."
  )
  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody RegistrationRequestDTO requestDTO) {
    authService.registerUser(requestDTO);
    return ResponseEntity.ok("User registered successfully");
  }
}