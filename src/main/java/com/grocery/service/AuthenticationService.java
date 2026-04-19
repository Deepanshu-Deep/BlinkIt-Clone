package com.grocery.service;

import com.grocery.dto.JWTAuthenticationResponse;
import com.grocery.dto.SignInRequest;
import com.grocery.dto.SignUpRequest;

public interface AuthenticationService {

    JWTAuthenticationResponse signup(SignUpRequest signUpRequest);
    JWTAuthenticationResponse signin(SignInRequest signInRequest);
    void resetPasswordSimple(String email, String mobile, String newPassword);
}
