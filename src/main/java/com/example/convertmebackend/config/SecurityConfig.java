package com.example.convertmebackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig{

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/checkLoggedIn").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/video/converter").authenticated()
                        .requestMatchers("/video/getVideoInfo").permitAll()
                        .requestMatchers("/audio/converter").authenticated()
                        .requestMatchers("/audio/getAudioInfo").permitAll()
                        .requestMatchers("/email/send").authenticated()
                )
                .logout(logout-> logout.logoutUrl("/logout").permitAll()
                        .logoutSuccessUrl("http://192.168.56.1:5500/loginPage.html").permitAll()
                )
                .formLogin(login ->login.permitAll()
                        .loginProcessingUrl("/auth/login").permitAll()
                        .loginPage("http://192.168.56.1:5500/loginPage.html").permitAll()
                        .defaultSuccessUrl("http://192.168.56.1:5500/Main.html",true).permitAll()

                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) throws Exception {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
    }


}
