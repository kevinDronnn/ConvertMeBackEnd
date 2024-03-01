
package com.example.convertmebackend.config;

import com.example.convertmebackend.entity.WebUser;
import com.example.convertmebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CustomUserDetails implements UserDetailsService {

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder encoder;


    @Autowired
    public CustomUserDetails(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        WebUser webUser = userService.findUserByEmail(username);
        if (webUser == null) {
            throw new UsernameNotFoundException("WebUser with email: " + username + " not found");
        }

        // Создаем список ролей пользователя
        List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(webUser.getRole()));

        // Возвращаем объект UserDetails с именем пользователя, паролем и ролями
        return new User(webUser.getEmail(), webUser.getPassword(), authorities);
    }

}
