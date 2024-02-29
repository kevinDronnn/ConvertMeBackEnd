package com.example.convertmebackend.service;

import com.example.convertmebackend.entity.WebUser;
import com.example.convertmebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public boolean existsUserByEmail(String email) {
        return userRepository.existsUserByEmail(email);
    }

    public WebUser findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
    @Transactional
    public void saveUser(WebUser webUser) {
        userRepository.save(webUser);
    }

    @Transactional
    public void removeUser(int id) {
        userRepository.deleteById(id);
    }

}
