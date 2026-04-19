package com.grocery.controller;

import com.grocery.dto.JWTAuthenticationResponse;
import com.grocery.dto.SignInRequest;
import com.grocery.dto.SignUpRequest;
import com.grocery.model.ResetPasswordRequest;
import com.grocery.model.User;
import com.grocery.service.AuthenticationService;
import com.grocery.service.JWTService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth") //
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    // SIGNUP
    @PostMapping("/signup")
    public ResponseEntity<JWTAuthenticationResponse> signUp(
            @Valid @RequestBody SignUpRequest request) {

        log.info("Signup request for email: {}", request.getEmail());

        JWTAuthenticationResponse response = authenticationService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // SIGNIN
    @PostMapping("/signin")
    public ResponseEntity<JWTAuthenticationResponse> signin(
            @Valid @RequestBody SignInRequest request) {

        log.info("Login attempt for: {}", request.getEmail());

        try {
            JWTAuthenticationResponse response = authenticationService.signin(request);

            log.info("Login successful for: {}", request.getEmail());

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            log.error("Login failed for: {}", request.getEmail());

            throw e;
        }
    }

    // BASIC RESET PASSWORD
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {

        log.info("Password reset attempt for email: {}", request.getEmail());

        authenticationService.resetPasswordSimple(
                request.getEmail(),
                request.getPhoneNumber(),
                request.getNewPassword()
        );

        return ResponseEntity.ok("Password updated successfully");
    }
}