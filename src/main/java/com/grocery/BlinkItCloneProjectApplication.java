package com.grocery;

import com.grocery.dto.UserDTO;
import com.grocery.enums.RoleType;
import com.grocery.model.User;
import com.grocery.repository.UserRepository;
import com.grocery.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.logging.Logger;


@Slf4j
@SpringBootApplication
@EnableAsync
public class BlinkItCloneProjectApplication{

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	public static void main(String[] args) {

        SpringApplication.run(BlinkItCloneProjectApplication.class, args);
	}


}
