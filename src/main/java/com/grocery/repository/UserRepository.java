package com.grocery.repository;

import com.grocery.enums.RoleType;
import com.grocery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByEmail(String email);

//    User findTopByRole(RoleType role);



}
