package com.grocery.serviceImpl;

import com.grocery.dto.JWTAuthenticationResponse;
import com.grocery.dto.SignInRequest;
import com.grocery.dto.SignUpRequest;
import com.grocery.enums.RoleType;
import com.grocery.model.User;
import com.grocery.repository.UserRepository;
import com.grocery.security.CustomUserDetails;
import com.grocery.service.AuthenticationService;
import com.grocery.service.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    @Override
    public JWTAuthenticationResponse signup(SignUpRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists with this email");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());

        user.setRole(request.getRole() != null ? request.getRole() : RoleType.USER);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        UserDetails userDetails = new CustomUserDetails(savedUser);

        String token = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), userDetails);

        return new JWTAuthenticationResponse(
                token,
                refreshToken,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName()
        );
    }


    public JWTAuthenticationResponse signin(SignInRequest request){

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = new CustomUserDetails(user);

        String token = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), userDetails);

        return new JWTAuthenticationResponse(
                token,
                refreshToken,
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }

    @Override
    public void resetPasswordSimple(String email, String mobile, String newPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPhoneNumber().equals(mobile)) {
            throw new RuntimeException("Invalid credentials");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
    }

}
