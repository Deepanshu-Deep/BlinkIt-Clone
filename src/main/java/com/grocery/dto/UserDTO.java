package com.grocery.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.grocery.enums.RoleType;
import lombok.Data;

@Data
public class UserDTO {

    private Long id;
    private String name;
    private String email;

    @JsonIgnore
    private String password;
    private String phoneNumber;
    private String address;
    private RoleType role;

}