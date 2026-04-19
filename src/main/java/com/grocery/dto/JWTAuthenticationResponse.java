package com.grocery.dto;

import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JWTAuthenticationResponse {

    private String token;
    private String refreshToken;
    private Long userId;
    private String email;
    private String userName;

}
