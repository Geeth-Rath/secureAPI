package com.secureAPI.secureAPI.repositoty;

import com.secureAPI.secureAPI.user.User;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Repository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
}
