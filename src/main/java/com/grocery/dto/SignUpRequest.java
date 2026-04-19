package com.grocery.dto;

import com.grocery.enums.RoleType;
import lombok.Data;

@Data
public class SignUpRequest {

    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private RoleType role;


}
