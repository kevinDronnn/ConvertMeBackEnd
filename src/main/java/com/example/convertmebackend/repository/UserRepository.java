package com.example.convertmebackend.repository;

import com.example.convertmebackend.entity.WebUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<WebUser, Integer> {
     Boolean existsUserByEmail(String email);
     WebUser findUserByEmail(String email);

}
