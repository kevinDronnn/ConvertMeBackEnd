package com.example.convertmebackend.controllers;

import com.example.convertmebackend.config.CustomUserDetails;
import com.example.convertmebackend.dto.LoginDto;
import com.example.convertmebackend.dto.RegisterDto;
import com.example.convertmebackend.entity.WebUser;
import com.example.convertmebackend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(AudioController.class);
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/loginUser")
    public ResponseEntity<String> loginUser(@RequestBody LoginDto loginDto) {

        WebUser user = userService.findUserByEmail(loginDto.getEmail());
        if (user == null || !passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        Authentication authenticated = authenticationManager.authenticate(authentication);

        SecurityContextHolder.getContext().setAuthentication(authenticated);

        if (authenticated.isAuthenticated()) {
            return new ResponseEntity<>("Login successful", HttpStatus.OK);
        }

        return new ResponseEntity<>("Login failed", HttpStatus.UNAUTHORIZED);
    }


    @PostMapping("/register")
    public ResponseEntity<String> addUser(@RequestBody RegisterDto registerDto) {
        if (userService.existsUserByEmail(registerDto.getEmail())) {
            return new ResponseEntity<>("this email is already taken", HttpStatus.BAD_REQUEST);
        }
        WebUser webUser = new WebUser();
        webUser.setEmail(registerDto.getEmail());
        webUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        webUser.setRole("USER");
        userService.saveUser(webUser);
        logger.info("User with email: " + registerDto.getEmail() + " has been created");
        return new ResponseEntity<>("Registration is success", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable(name = "id") int id) {
        userService.removeUser(id);
        logger.info("User with id: " + id + " has been deleted");
    }

}
